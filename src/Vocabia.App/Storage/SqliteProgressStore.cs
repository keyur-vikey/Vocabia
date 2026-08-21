using SQLite;
using Vocabia.Core.Models;
using Vocabia.Core.Storage;

namespace Vocabia.App.Storage;

[Table("user_word_progress")]
internal sealed class ProgressRow
{
    [PrimaryKey] public string WordId { get; set; } = "";
    public int BoxLevel { get; set; }
    public int ConsecutiveCorrect { get; set; }
    public int NextEligibleAt { get; set; }
    public string Status { get; set; } = ProgressStatus.Learning;
    public int TimesSeen { get; set; }

    public UserWordProgress ToModel() => new()
    {
        WordId = WordId,
        BoxLevel = BoxLevel,
        ConsecutiveCorrect = ConsecutiveCorrect,
        NextEligibleAt = NextEligibleAt,
        Status = Status,
        TimesSeen = TimesSeen,
    };

    public static ProgressRow FromModel(UserWordProgress p) => new()
    {
        WordId = p.WordId,
        BoxLevel = p.BoxLevel,
        ConsecutiveCorrect = p.ConsecutiveCorrect,
        NextEligibleAt = p.NextEligibleAt,
        Status = p.Status,
        TimesSeen = p.TimesSeen,
    };
}

[Table("session_counter")]
internal sealed class SessionCounterRow
{
    [PrimaryKey] public int Id { get; set; }
    public int CurrentSession { get; set; }
}

/// <summary>
/// SQLite-backed progress store for the native app heads (Android, iOS, Mac Catalyst, Windows).
/// The database lives in the platform's app data directory.
/// </summary>
public sealed class SqliteProgressStore : IProgressStore
{
    private readonly SQLiteAsyncConnection _db;
    private readonly SemaphoreSlim _initLock = new(1, 1);
    private bool _initialized;

    public SqliteProgressStore(string databasePath)
    {
        _db = new SQLiteAsyncConnection(databasePath);
    }

    private async Task EnsureInitializedAsync()
    {
        if (_initialized) return;
        await _initLock.WaitAsync();
        try
        {
            if (_initialized) return;
            await _db.CreateTableAsync<ProgressRow>();
            await _db.CreateTableAsync<SessionCounterRow>();
            if (await _db.Table<SessionCounterRow>().Where(r => r.Id == 0).FirstOrDefaultAsync() is null)
            {
                await _db.InsertAsync(new SessionCounterRow { Id = 0, CurrentSession = 0 });
            }
            _initialized = true;
        }
        finally
        {
            _initLock.Release();
        }
    }

    public async Task<List<UserWordProgress>> GetAllAsync()
    {
        await EnsureInitializedAsync();
        var rows = await _db.Table<ProgressRow>().ToListAsync();
        return rows.Select(r => r.ToModel()).ToList();
    }

    public async Task<UserWordProgress?> GetAsync(string wordId)
    {
        await EnsureInitializedAsync();
        var row = await _db.Table<ProgressRow>().Where(r => r.WordId == wordId).FirstOrDefaultAsync();
        return row?.ToModel();
    }

    public async Task UpsertAsync(UserWordProgress progress)
    {
        await EnsureInitializedAsync();
        await _db.InsertOrReplaceAsync(ProgressRow.FromModel(progress));
    }

    public async Task<int> GetSessionCounterAsync()
    {
        await EnsureInitializedAsync();
        var row = await _db.Table<SessionCounterRow>().Where(r => r.Id == 0).FirstOrDefaultAsync();
        return row?.CurrentSession ?? 0;
    }

    public async Task SetSessionCounterAsync(int value)
    {
        await EnsureInitializedAsync();
        await _db.InsertOrReplaceAsync(new SessionCounterRow { Id = 0, CurrentSession = value });
    }
}
