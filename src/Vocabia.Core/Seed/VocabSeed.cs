using System.Reflection;
using System.Text.Json;
using System.Linq;
using Vocabia.Core.Models;

namespace Vocabia.Core.Seed;

/// <summary>
/// The pilot German/English word list, bundled as an embedded resource so every
/// platform head (MAUI native, Blazor WebAssembly) gets it for free with no
/// platform-specific asset wiring.
/// </summary>
public static class VocabSeed
{
    private const string ResourceName = "Vocabia.Core.Seed.Resources.de_en_pilot.json";

    private static readonly Lazy<IReadOnlyList<WordEntry>> LazyWords = new(LoadWords);

    public static IReadOnlyList<WordEntry> Words => LazyWords.Value;

    private static IReadOnlyList<WordEntry> LoadWords()
    {
        var assembly = typeof(VocabSeed).Assembly;
        using var stream = assembly.GetManifestResourceStream(ResourceName)
            ?? throw new InvalidOperationException($"Embedded resource '{ResourceName}' not found.");
        using var document = JsonDocument.Parse(stream);

        var words = new List<WordEntry>(document.RootElement.GetArrayLength());
        foreach (var element in document.RootElement.EnumerateArray())
        {
            words.Add(ParseWord(element));
        }
        return words;
    }

    private static WordEntry ParseWord(JsonElement o)
    {
        var sentences = o.GetProperty("sentences").EnumerateArray().Select(e => e.GetString()).ToArray();
        var sentenceTranslations = o.TryGetProperty("sentence_translations", out var st)
            ? st.EnumerateArray().Select(e => e.ValueKind == JsonValueKind.Null ? null : e.GetString()).ToArray()
            : null;
        var verbInfo = o.TryGetProperty("verb_info", out var vi) ? vi : (JsonElement?)null;
        var presentTense = verbInfo?.TryGetProperty("present_tense", out var pt) == true ? pt : (JsonElement?)null;
        var nounInfo = o.TryGetProperty("noun_info", out var ni) ? ni : (JsonElement?)null;
        var adjectiveInfo = o.TryGetProperty("adjective_info", out var ai) ? ai : (JsonElement?)null;
        var prepositionInfo = o.TryGetProperty("preposition_info", out var pi) ? pi : (JsonElement?)null;

        return new WordEntry
        {
            Id = o.GetProperty("id").GetString()!,
            Language = o.GetProperty("language").GetString()!,
            BaseLanguage = o.TryGetProperty("base_language", out var bl) ? bl.GetString() ?? "en" : "en",
            Word = o.GetProperty("word").GetString()!,
            Article = OptString(o, "article"),
            Translation = o.GetProperty("translation").GetString()!,
            Category = o.GetProperty("category").GetString()!,
            FrequencyRank = o.GetProperty("frequency_rank").GetInt32(),
            CefrLevel = OptString(o, "cefr_level"),
            Sentence1 = sentences[0]!,
            Sentence2 = sentences[1]!,
            Sentence3 = sentences[2]!,
            SentenceTranslation1 = ArrayItem(sentenceTranslations, 0),
            SentenceTranslation2 = ArrayItem(sentenceTranslations, 1),
            SentenceTranslation3 = ArrayItem(sentenceTranslations, 2),
            VerbPerfectForm = OptString(verbInfo, "perfect_form"),
            VerbAuxiliary = OptString(verbInfo, "auxiliary"),
            VerbSeparable = verbInfo?.TryGetProperty("separable", out var sep) == true ? sep.GetBoolean() : null,
            VerbSeparablePrefix = OptString(verbInfo, "separable_prefix"),
            VerbPresentIch = OptString(presentTense, "ich"),
            VerbPresentDu = OptString(presentTense, "du"),
            VerbPresentErSieEs = OptString(presentTense, "er_sie_es"),
            VerbPresentWir = OptString(presentTense, "wir"),
            VerbPresentIhr = OptString(presentTense, "ihr"),
            VerbPresentSieSie = OptString(presentTense, "sie_Sie"),
            NounPlural = OptString(nounInfo, "plural"),
            NounGender = OptString(nounInfo, "gender"),
            AdjectiveComparative = OptString(adjectiveInfo, "comparative"),
            AdjectiveSuperlative = OptString(adjectiveInfo, "superlative"),
            PrepositionCase = OptString(prepositionInfo, "case"),
        };
    }

    private static string? OptString(JsonElement? element, string key)
    {
        if (element is not { } e) return null;
        if (!e.TryGetProperty(key, out var value)) return null;
        return value.ValueKind == JsonValueKind.Null ? null : value.GetString();
    }

    private static string? ArrayItem(string?[]? array, int index) =>
        array is not null && index < array.Length ? array[index] : null;
}
