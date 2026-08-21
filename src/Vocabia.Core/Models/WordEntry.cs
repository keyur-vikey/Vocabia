namespace Vocabia.Core.Models;

public sealed class WordEntry
{
    public required string Id { get; init; }
    public required string Language { get; init; }
    public string BaseLanguage { get; init; } = "en";
    public required string Word { get; init; }
    public string? Article { get; init; }
    public required string Translation { get; init; }
    public required string Category { get; init; }
    public int FrequencyRank { get; init; }
    public string? CefrLevel { get; init; }

    public required string Sentence1 { get; init; }
    public required string Sentence2 { get; init; }
    public required string Sentence3 { get; init; }
    public string? SentenceTranslation1 { get; init; }
    public string? SentenceTranslation2 { get; init; }
    public string? SentenceTranslation3 { get; init; }

    // verb_info
    public string? VerbPerfectForm { get; init; }
    public string? VerbAuxiliary { get; init; }
    public bool? VerbSeparable { get; init; }
    public string? VerbSeparablePrefix { get; init; }
    public string? VerbPresentIch { get; init; }
    public string? VerbPresentDu { get; init; }
    public string? VerbPresentErSieEs { get; init; }
    public string? VerbPresentWir { get; init; }
    public string? VerbPresentIhr { get; init; }
    public string? VerbPresentSieSie { get; init; }

    // noun_info
    public string? NounPlural { get; init; }
    public string? NounGender { get; init; }

    // adjective_info
    public string? AdjectiveComparative { get; init; }
    public string? AdjectiveSuperlative { get; init; }

    // preposition_info
    public string? PrepositionCase { get; init; }
}
