using System.Text.Json;
using Microsoft.JSInterop;
using Vocabia.Core.Models;
using Vocabia.Core.Storage;

namespace Vocabia.Web.Storage;

/// <summary>
/// Browser-local progress store for the Blazor WebAssembly (Web) head. Persists to
/// localStorage as JSON, since running SQLite in the browser needs no benefit here:
/// the only mutable state is a small list of per-word progress rows and a counter.
/// </summary>
public sealed class LocalStorageProgressStore(IJSRuntime js) : IProgressStore
{
    private const string ProgressKey = "vocabia.progress";
    private const string SessionKey = "vocabia.session";

    private List<UserWordProgress>? _cache;
    private readonly SemaphoreSlim _lock = new(1, 1);

    private async Task<List<UserWordProgress>> LoadAsync()
    {
        if (_cache is not null) return _cache;
        await _lock.WaitAsync();
        try
        {
            if (_cache is not null) return _cache;
            var json = await js.InvokeAsync<string?>("localStorage.getItem", ProgressKey);
            _cache = string.IsNullOrEmpty(json)
                ? []
                : JsonSerializer.Deserialize<List<UserWordProgress>>(json) ?? [];
            return _cache;
        }
        finally
        {
            _lock.Release();
        }
    }

    private async Task SaveAsync()
    {
        var json = JsonSerializer.Serialize(_cache ?? []);
        await js.InvokeVoidAsync("localStorage.setItem", ProgressKey, json);
    }

    public async Task<List<UserWordProgress>> GetAllAsync() => [.. await LoadAsync()];

    public async Task<UserWordProgress?> GetAsync(string wordId) =>
        (await LoadAsync()).FirstOrDefault(p => p.WordId == wordId);

    public async Task UpsertAsync(UserWordProgress progress)
    {
        var all = await LoadAsync();
        all.RemoveAll(p => p.WordId == progress.WordId);
        all.Add(progress);
        await SaveAsync();
    }

    public async Task<int> GetSessionCounterAsync()
    {
        var raw = await js.InvokeAsync<string?>("localStorage.getItem", SessionKey);
        return int.TryParse(raw, out var value) ? value : 0;
    }

    public async Task SetSessionCounterAsync(int value)
    {
        await js.InvokeVoidAsync("localStorage.setItem", SessionKey, value.ToString());
    }
}
