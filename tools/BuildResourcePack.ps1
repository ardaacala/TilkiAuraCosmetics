$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$jar = Join-Path $root '.tools/jdk-21.0.12.1+1/bin/jar.exe'
if (-not (Test-Path -LiteralPath $jar)) {
    $jar = (Get-Command jar -ErrorAction Stop).Source
}
$pack = Join-Path $root 'dist/AuraCosmetics-TilkiInterface-ResourcePack.zip'
New-Item -ItemType Directory -Path (Split-Path $pack) -Force | Out-Null
Push-Location $root
try {
    & $jar --create --file $pack --no-manifest -C resourcepack pack.mcmeta -C resourcepack pack.png -C resourcepack assets
    if ($LASTEXITCODE -ne 0) { throw 'Resource pack archive failed.' }
    (Get-FileHash -Algorithm SHA1 -LiteralPath $pack).Hash.ToLowerInvariant() |
        Set-Content -Encoding ascii (Join-Path $root 'dist/AuraCosmetics-TilkiInterface-ResourcePack.sha1')
} finally {
    Pop-Location
}
