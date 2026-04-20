$parts = @{
    "cabeza" = "M12,2 C10.9,2 10,2.9 10,4 C10,5.1 10.9,6 12,6 C13.1,6 14,5.1 14,4 C14,2.9 13.1,2 12,2 Z"
    "trapecio" = "M10,6 L14,6 L16,8 L8,8 Z"
    "hombro" = "M5,8 L8,8 L8,11 L4,11 Z M16,8 L19,8 L20,11 L16,11 Z"
    "pecho" = "M8,8 L16,8 L15.5,12 L8.5,12 Z"
    "espalda" = "M8,8 L16,8 L15.5,12 L8.5,12 Z"
    "abdomen" = "M8.5,12 L15.5,12 L15,16 L9,16 Z"
    "biceps" = "M4,11 L8,11 L7.5,15 L3.5,15 Z M16,11 L20,11 L20.5,15 L16.5,15 Z"
    "triceps" = "M4,11 L8,11 L7.5,15 L3.5,15 Z M16,11 L20,11 L20.5,15 L16.5,15 Z"
    "antebrazo" = "M3.5,15 L7.5,15 L7,20 L4,20 Z M16.5,15 L20.5,15 L20,20 L17,20 Z"
    "gluteo" = "M9,16 L15,16 L15,18 L9,18 Z"
    "pierna" = "M9,18 L11.5,18 L11.5,25 L8.5,25 Z M12.5,18 L15,18 L15.5,25 L12.5,25 Z"
    "gemelo" = "M8.5,25 L11.5,25 L11,31 L9,31 Z M12.5,25 L15.5,25 L15,31 L13,31 Z"
}

$muscles = @("pecho", "espalda", "hombro", "biceps", "triceps", "antebrazo", "abdomen", "pierna", "gluteo", "gemelo", "trapecio")

foreach ($target in $muscles) {
    # Generate XML
    $xml = "<?xml version=`"1.0`" encoding=`"utf-8`"?>`n"
    $xml += "<vector xmlns:android=`"http://schemas.android.com/apk/res/android`"`n"
    $xml += "    android:width=`"200dp`"`n"
    $xml += "    android:height=`"266dp`"`n"
    $xml += "    android:viewportWidth=`"24`"`n"
    $xml += "    android:viewportHeight=`"32`">`n"
    
    foreach ($key in $parts.Keys) {
        $pathData = $parts[$key]
        $color = "#E0E0E0" # Default grey
        if ($key -eq $target) {
            $color = "#FF0000" # Target muscle is red
        }
        # Pecho and Espalda share the same path, Biceps/Triceps too. 
        # But we only highlight the $target.
        $xml += "    <path`n"
        $xml += "        android:fillColor=`"$color`"`n"
        $xml += "        android:pathData=`"$pathData`" />`n"
    }

    $xml += "</vector>"

    $filepath = "app/src/main/res/drawable/maniqui_$target.xml"
    Set-Content -Path $filepath -Value $xml -Encoding UTF8 -Force
}
