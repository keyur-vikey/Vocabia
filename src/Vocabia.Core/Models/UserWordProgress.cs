namespace Vocabia.Core.Models;

public static class ProgressStatus
{
    public const string Learning = "learning";
    public const string Finished = "finished";
}

public sealed record UserWordProgress
{
    public required string WordId { get; init; }
    public int BoxLevel { get; init; }
    public int ConsecutiveCorrect { get; init; }
    public int NextEligibleAt { get; init; }
    public string Status { get; init; } = ProgressStatus.Learning;
    public int TimesSeen { get; init; }

    public static UserWordProgress Default(string wordId) => new() { WordId = wordId };
}
