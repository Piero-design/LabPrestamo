# Registro G01 - LabPrestamo. Ejecutar desde la carpeta raiz del repositorio.
$ErrorActionPreference = 'Stop'
$repo = 'Piero-design/LabPrestamo'
$folder = Join-Path (Get-Location) 'guias\G01'
$ers = Join-Path $folder 'ERS_G01.md'
if (-not (Test-Path $ers)) { throw 'Ejecuta el script desde la raiz de LabPrestamo.' }
& gh auth status --hostname github.com
if ($LASTEXITCODE -ne 0) { throw 'Primero inicia sesion con gh auth login.' }
function Get-Issues {
    $json = & gh issue list --repo $repo --state all --limit 1000 --json number,title,url,state,assignees,createdAt,updatedAt
    if ($LASTEXITCODE -ne 0) { throw 'No se pudo consultar GitHub Issues.' }
    return ($json -join "`n" | ConvertFrom-Json)
}
$items = @()
foreach ($line in Get-Content $ers -Encoding UTF8) {
    if ($line -notmatch '^\|\s*(RF|RNF)-\d{2}\s*\|') { continue }
    $c = $line.Trim().Trim('|').Split('|') | ForEach-Object { $_.Trim() }
    $id = $c[0]
    if ($id -like 'RF-*') {
        if ($c.Count -ne 5) { throw "Fila ERS invalida: $id" }
        $title = $c[2]; $actor = $c[1]; $criterion = $c[3]; $test = $c[4]
    } else {
        if ($c.Count -ne 4) { throw "Fila ERS invalida: $id" }
        $title = $c[1]; $actor = 'Sistema / equipo de calidad'; $criterion = $c[2]; $test = $c[3]
    }
    $body = @"
## Requisito $id

- Descripción: $title
- Actor: $actor
- Prioridad: Alta (propuesta en ERS).
- Fuente: propuesta técnica Hito 1 y ERS G01, versión 0.3-borrador.
- Responsable de especificación: Jael Emerson Huarca Pallani (G01).
- Responsable de seguimiento de cambios: Franco Calcina Flores (G02).
- Responsable de implementación: por confirmar con el equipo.
- Estado inicial de gestión: registrado, pendiente de validación de interesados.

## Criterio de aceptación
$criterion

## Prueba o evidencia prevista
$test

El registro de esta incidencia no acredita el cumplimiento completo ni la aprobación del requisito. Fuente editable: guias/G01/ERS_G01.md (rama de trabajo G01/G06 al preparar este registro).
"@
    $items += [pscustomobject]@{ID=$id; Title="$id - $title"; Body=$body; Type='Requisito'}
}
if ($items.Count -ne 15 -or @($items.ID | Sort-Object -Unique).Count -ne 15) { throw 'La ERS debe contener exactamente 15 requisitos con IDs unicos.' }
$items += [pscustomobject]@{ID='DEF-G01-01'; Title='DEF-G01-01 - Mensaje incorrecto durante aprobacion de reserva'; Type='Defecto'; Body=@"
- Fecha de observación: 24/11/2026.
- Requisito: RF-07.
- Versión de LabPréstamo: 0.2.0; revisión Git evaluada: 5a6840d.
- Datos: solicitud #1, equipo EQ-001.
- Pasos: entrar como encargado, seleccionar una solicitud pendiente, pulsar Aprobar y Guardar.
- Esperado: estado APROBADA y confirmación coherente.
- Observado: mensaje «El código del equipo ya existe o los datos no son válidos». Tras reiniciar y volver a entrar, se observó APROBADA.
- Severidad propuesta: menor; prioridad propuesta: media.
- Responsable de diagnóstico: pendiente de asignación.
- Estado: abierto; causa raíz y prueba de cierre pendientes.
- Evidencia local: guias/G01/Evidencias/Evidencia_Error_Aprobacion_RF07.png (se incorporará con G01).

No se declara corregido. Comunicar a G02 y G09 para seguimiento.
"@
}
$existing = @(Get-Issues)
$duplicates = @($items | Where-Object { $id = $_.ID; @($existing | Where-Object { $_.title -match ('^' + [regex]::Escape($id) + '\b') }).Count -gt 1 })
if ($duplicates.Count -gt 0) { throw 'Hay IDs repetidos en GitHub. Revisa antes de continuar.' }
foreach ($item in $items) {
    $pattern = '^' + [regex]::Escape($item.ID) + '\b'
    $match = @($existing | Where-Object { $_.title -match $pattern })
    if ($match.Count -gt 0) { Write-Host "Ya existe $($item.ID): $($match[0].url)"; continue }
    $temp = [IO.Path]::GetTempFileName()
    try {
        [IO.File]::WriteAllText($temp, $item.Body, ([System.Text.UTF8Encoding]::new($false)))
        $url = & gh issue create --repo $repo --title $item.Title --body-file $temp
        if ($LASTEXITCODE -ne 0) { throw "No se pudo crear $($item.ID). Puedes volver a ejecutar sin repetir IDs existentes." }
        Write-Host "Registrado $($item.ID): $url" -ForegroundColor Green
    } finally { Remove-Item $temp -Force }
}
$all = @(Get-Issues)
$records = foreach ($item in $items) {
    $pattern = '^' + [regex]::Escape($item.ID) + '\b'
    $match = @($all | Where-Object { $_.title -match $pattern })
    if ($match.Count -ne 1) { throw "No se encontro una incidencia unica para $($item.ID)." }
    $i = $match[0]
    [pscustomobject]@{ID=$item.ID; Tipo=$item.Type; Numero=$i.number; Titulo=$i.title; EstadoGitHub=$i.state; URL=$i.url; AsignadosGitHub=(@($i.assignees | ForEach-Object { $_.login }) -join '; '); FechaCreacion=$i.createdAt; FechaActualizacion=$i.updatedAt; FechaExportacion=(Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')}
}
$records | Export-Csv (Join-Path $folder 'Registro_Requisitos_G01.csv') -NoTypeInformation -Encoding UTF8
$records | Format-Table ID,Numero,EstadoGitHub,URL -AutoSize
Write-Host 'Exportado: guias/G01/Registro_Requisitos_G01.csv' -ForegroundColor Green
