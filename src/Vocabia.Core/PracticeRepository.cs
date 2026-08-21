using Vocabia.Core.Models;
using Vocabia.Core.Seed;
using Vocabia.Core.Storage;

namespace Vocabia.Core;

public enum SwipeDirection { Left, Right, Down }

public sealed record PracticeCard(WordEntry Word, UserWordProgress Progress);

public sealed class PracticeRepository(IProgressStore store)
{
    private const int SessionTargetSize = 12;
    private const int NewWordCap = 5;
    private const double DueShare = 0.7;

    private static int DelayForBox(int boxLevel) => boxLevel switch
    {
        1 => 2,
        2 => 3,
        3 => 5,
        4 => 8,
        _ => 12,
    };

    public async Task<List<PracticeCard>> BuildSessionAsync()
    {
        var allWords = VocabSeed.Words;
        var progressByWord = (await store.GetAllAsync()).ToDictionary(p => p.WordId);
        var currentSession = await store.GetSessionCounterAsync();

        var duePool = allWords
            .Select(word => (word, progress: progressByWord.GetValueOrDefault(word.Id)))
            .Where(t => t.progress is { Status: ProgressStatus.Learning } p && p.NextEligibleAt <= currentSession)
            .OrderBy(t => t.progress!.NextEligibleAt)
            .ToList();

        var newPool = allWords
            .Where(w => !progressByWord.ContainsKey(w.Id))
            .OrderBy(w => w.FrequencyRank)
            .ToList();

        var dueSlots = (int)(SessionTargetSize * DueShare);
        var fromDue = duePool.Take(dueSlots).ToList();
        var remainingSlots = SessionTargetSize - fromDue.Count;
        var fromNew = newPool.Take(Math.Min(remainingSlots, NewWordCap)).ToList();

        var selected = fromDue
            .Select(t => (t.word, progress: t.progress!))
            .Concat(fromNew.Select(w => (w, progress: UserWordProgress.Default(w.Id))))
            .ToList();

        if (selected.Count < SessionTargetSize)
        {
            var already = selected.Select(s => s.word.Id).ToHashSet();
            var topUp = newPool
                .Where(w => !already.Contains(w.Id))
                .Take(SessionTargetSize - selected.Count)
                .Select(w => (w, progress: UserWordProgress.Default(w.Id)));
            selected.AddRange(topUp);
        }

        var random = Random.Shared;
        return selected
            .OrderBy(_ => random.Next())
            .Select(s => new PracticeCard(s.word, s.progress))
            .ToList();
    }

    public async Task RecordSwipeAsync(WordEntry word, SwipeDirection direction)
    {
        var currentSession = await store.GetSessionCounterAsync();
        var existing = await store.GetAsync(word.Id) ?? UserWordProgress.Default(word.Id);
        var timesSeen = existing.TimesSeen + 1;

        var updated = direction switch
        {
            SwipeDirection.Left => existing with
            {
                ConsecutiveCorrect = 0,
                BoxLevel = Math.Max(0, existing.BoxLevel - 1),
                NextEligibleAt = currentSession + 1,
                Status = ProgressStatus.Learning,
                TimesSeen = timesSeen,
            },
            SwipeDirection.Right => RecordCorrect(existing, currentSession, timesSeen),
            SwipeDirection.Down => existing with
            {
                Status = ProgressStatus.Finished,
                TimesSeen = timesSeen,
            },
            _ => throw new ArgumentOutOfRangeException(nameof(direction)),
        };

        await store.UpsertAsync(updated);
    }

    private static UserWordProgress RecordCorrect(UserWordProgress existing, int currentSession, int timesSeen)
    {
        var newConsecutive = existing.ConsecutiveCorrect + 1;
        var newBox = Math.Min(5, existing.BoxLevel + 1);
        return existing with
        {
            ConsecutiveCorrect = newConsecutive,
            BoxLevel = newBox,
            NextEligibleAt = currentSession + DelayForBox(newBox),
            Status = newConsecutive >= 3 ? ProgressStatus.Finished : ProgressStatus.Learning,
            TimesSeen = timesSeen,
        };
    }

    public async Task AdvanceSessionAsync()
    {
        var current = await store.GetSessionCounterAsync();
        await store.SetSessionCounterAsync(current + 1);
    }

    public async Task<(int Finished, int Total)> StatsAsync()
    {
        var total = VocabSeed.Words.Count;
        var finished = (await store.GetAllAsync()).Count(p => p.Status == ProgressStatus.Finished);
        return (finished, total);
    }

    public sealed record CategoryStats(string Category, int Finished, int Learning, int New, int Total);

    public async Task<List<CategoryStats>> StatsByCategoryAsync()
    {
        var progressByWord = (await store.GetAllAsync()).ToDictionary(p => p.WordId);

        return VocabSeed.Words
            .GroupBy(w => w.Category)
            .Select(group =>
            {
                int finished = 0, learning = 0, @new = 0;
                foreach (var word in group)
                {
                    var progress = progressByWord.GetValueOrDefault(word.Id);
                    if (progress is null) @new++;
                    else if (progress.Status == ProgressStatus.Finished) finished++;
                    else learning++;
                }
                return new CategoryStats(group.Key, finished, learning, @new, group.Count());
            })
            .OrderByDescending(s => s.Total)
            .ToList();
    }
}
