function splitGeoJson {
    param (
        [string]$InputFile = "runs",
        [string]$OutputDirectory = "GeoJsons"
    )
    $invalidChars = "[" + ([regex]::Escape([string]::Join('', [System.IO.Path]::GetInvalidFileNameChars()))) + "]"
    # Create the output directory if it doesn't exist
    if (-not (Test-Path $OutputDirectory)) {
        New-Item -Path $OutputDirectory -ItemType Directory | Out-Null
    }

    Write-Host "Reading JSON"
    # 1. Read and convert the JSON file contents to PowerShell objects
    $jsonData = Get-Content -Raw -Path "$InputFile.geojson" | ConvertFrom-Json

    Write-Host "Grouping JSON"
    # 2. Group the objects by the specified property
    $groupedData = $jsonData.features | Group-Object -Property {
        $place = $_.properties.places[0].localized.en
        "$($place.country)|$($place.region)"
    }

    Write-Host 'Iterating Through Groups'
    # 3. Iterate through each group and export the items to a new JSON file
    foreach ($group in $groupedData) {
        # Define the output file name based on the group name (property value)
        $index = $group.Name.IndexOf("|")
        $country = $group.Name.Substring(0, $index)
        $region = $group.Name.Substring($index + 1)

        $countryFolder = Join-Path -Path $OutputDirectory -ChildPath $country 
        if (-not (Test-Path $countryFolder)) {
            New-Item -Path $countryFolder -ItemType Directory | Out-Null
        }
    
        $outputFileName = ("$($InputFile)_$($region).geojson") -replace $invalidChars, ""
        $outputPath = Join-Path -Path $countryFolder -ChildPath $outputFileName

        $group.Group | ConvertTo-Json -Depth 100 | Set-Content -Path $outputPath
        Write-Host "Created file: $outputPath"
    }

    Write-Host "JSON splitting complete."
}

splitGeoJson -InputFile "ski_areas"
splitGeoJson -InputFile "runs"
splitGeoJson -InputFile "lifts" 