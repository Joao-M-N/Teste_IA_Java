param([switch]$Test)
$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
        throw 'Instale o JDK 17 ou superior e adicione seu diretório bin ao PATH.'
    }
    New-Item -ItemType Directory -Force -Path 'build/classes' | Out-Null
    $sourceFiles = @(Get-ChildItem 'src/main/java' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
    if ($Test) {
        $sourceFiles += @(Get-ChildItem 'src/test/java' -Recurse -Filter '*.java' | ForEach-Object { $_.FullName })
    }
    & javac --release 17 -encoding UTF-8 -d 'build/classes' @sourceFiles
    if ($LASTEXITCODE -ne 0) { throw 'Falha na compilação.' }
    $mainClass = if ($Test) { 'br.com.patternsshop.CheckoutTest' } else { 'br.com.patternsshop.Main' }
    & java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Dstderr.encoding=UTF-8' -cp 'build/classes' $mainClass
    if ($LASTEXITCODE -ne 0) { throw 'Falha na execução.' }
} finally {
    Pop-Location
}
