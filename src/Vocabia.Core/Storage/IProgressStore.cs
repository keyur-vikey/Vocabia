using Vocabia.Core.Models;

namespace Vocabia.Core.Storage;

/// <summary>
/// Persists the only mutable state in the app: per-word learning progress and the
/// current session counter. Word content itself is static seed data (see VocabSeed)
/// and never needs to be written back, so it is not part of this abstraction.
/// </summary>
public interface IProgressStore
{
    Task<List<UserWordProgress>> GetAllAsync();
    Task<UserWordProgress?> GetAsync(string wordId);
    Task UpsertAsync(UserWordProgress progress);
    Task<int> GetSessionCounterAsync();
    Task SetSessionCounterAsync(int value);
}
