namespace Vocabia.Shared;

public static class CategoryStyle
{
    public static string Color(string category) => category switch
    {
        "noun" => "#3B82F6",
        "verb" => "#F97316",
        "adjective" => "#22C55E",
        "preposition" => "#A855F7",
        _ => "#6B7280",
    };

    public static string Label(string category) => category switch
    {
        "noun" => "Noun",
        "verb" => "Verb",
        "adjective" => "Adjective",
        "preposition" => "Preposition",
        "adverb" => "Adverb",
        _ => "Other",
    };

    public static string Icon(string category) => category switch
    {
        "noun" => "\U0001F4D6",       // 📖
        "verb" => "\U0001F3C3",       // 🏃
        "adjective" => "\U0001F3A8",  // 🎨
        "preposition" => "\U0001F500", // 🔀
        _ => "\U0001F3F7",            // 🏷
    };
}
