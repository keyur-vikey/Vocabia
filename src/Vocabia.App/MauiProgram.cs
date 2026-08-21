using Microsoft.Extensions.Logging;
using Vocabia.App.Storage;
using Vocabia.Core;
using Vocabia.Core.Storage;

namespace Vocabia.App;

public static class MauiProgram
{
    public static MauiApp CreateMauiApp()
    {
        var builder = MauiApp.CreateBuilder();
        builder.UseMauiApp<App>();

        builder.Services.AddMauiBlazorWebView();

        var databasePath = Path.Combine(FileSystem.AppDataDirectory, "vocabia.db3");
        builder.Services.AddSingleton<IProgressStore>(_ => new SqliteProgressStore(databasePath));
        builder.Services.AddSingleton<PracticeRepository>();

#if DEBUG
        builder.Services.AddBlazorWebViewDeveloperTools();
        builder.Logging.AddDebug();
#endif

        return builder.Build();
    }
}
