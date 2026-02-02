function splitGeoJson {
    param (
        [string]$InputFile = "runs",
        [string]$CountryFilter = "",
        [string]$RegionFilter = "",
        [string]$SkiAreaFilter = "",
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
    # 2. Group the objects by country, region, and ski area
    if ($jsonData.features[0].properties.type -eq "skiArea") {
        $groupedData = $jsonData.features | Group-Object -Property {
            $place = $_.properties.places[0].localized.en
            $skiArea = $_.properties.name
            "$($place.country)|$($place.region)|$($skiArea)"
        }
    }
    else {
        $groupedData = $jsonData.features | Group-Object -Property {
            $place = $_.properties.places[0].localized.en
            $skiArea = $_.properties.skiAreas[0].properties.name
            "$($place.country)|$($place.region)|$($skiArea)"
        }
    }

    Write-Host 'Iterating Through Groups'
    # 3. Iterate through each group and export the items to a new JSON file
    foreach ($group in $groupedData) {

        # Define the output file name based on the group name (property value)
        $index = $group.Name.IndexOf("|")
        $indexTwo = $group.Name.Substring($group.Name.IndexOf("|") + 1).IndexOf("|") + $index

        $country = $group.Name.Substring(0, $index)
        if ($country -eq "") { $country = "null" }
        $region = $group.Name.Substring($index + 1, $indexTwo - $index)
        if ($region -eq "") { $region = "null" }
        $skiArea = $group.Name.Substring($indexTwo + 1)
        if ($skiArea -eq "") { $skiArea = "null" }

        
        if ($CountryFilter -ne "" -and -not $CountryFilter.Contains($country)) {
            continue
        }
        if ($RegionFilter -ne "" -and -not $RegionFilter.Contains($region)) {
            continue
        }
        if ($SkiAreaFilter -ne "" -and -not $SkiAreaFilter.Contains($skiArea)) {
            continue
        }
        
        $countryFolder = Join-Path -Path $OutputDirectory -ChildPath $country 
        if (-not (Test-Path $countryFolder)) {
            New-Item -Path $countryFolder -ItemType Directory | Out-Null
        }
        
        $regionFolder = Join-Path -Path $countryFolder -ChildPath $region
        if (-not (Test-Path $regionFolder)) {
            New-Item -Path $regionFolder -ItemType Directory | Out-Null
        }
    
        $outputFileName = ("$($InputFile)_$($skiArea).geojson") -replace $invalidChars, ""
        $outputPath = Join-Path -Path $regionFolder -ChildPath $outputFileName

        $group.Group | ConvertTo-Json -Depth 100 | Set-Content -Path $outputPath
        Write-Host "Created file: $outputPath"
    }

    Write-Host "JSON splitting complete."
}

splitGeoJson -InputFile "ski_areas"
splitGeoJson -InputFile "runs"
splitGeoJson -InputFile "lifts" 