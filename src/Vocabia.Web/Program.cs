using Microsoft.AspNetCore.Components.WebAssembly.Hosting;
using Vocabia.Core;
using Vocabia.Core.Storage;
using Vocabia.Web.Storage;

var builder = WebAssemblyHostBuilder.CreateDefault(args);

builder.RootComponents.Add<Vocabia.Shared.VocabiaApp>("#app");

builder.Services.AddScoped<IProgressStore, LocalStorageProgressStore>();
builder.Services.AddScoped<PracticeRepository>();

await builder.Build().RunAsync();
