# CalendarToolkit
Simple command line tools used to help generate and make calendars for a local sports association

# Build
`sbt assembly`

# Run
`java -jar CalendarToolkit-assembly-x.x.jar <arguments>`

### Java version

The program is best run with Java8, with newer Java versions you will get the following error but this won't affect the execution of the program.
```
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by scala.reflect.package$ (.../CalendarToolkit-assembly-1.0.jar) to method sun.nio.ch.ChannelInputStream.close()
WARNING: Please consider reporting this to the maintainers of scala.reflect.package$
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release
```

# Usage
````
Calendar Toolkit
Usage: calendarToolkit [generateFromConfig|generateFromArgs]

Command: generateFromConfig [options]
Generates the calendar batches from the configuration file
  -c, --configurationFile <value>
                           Calendar batches configuration file
Command: generateFromArgs [options]
Generates the calendar batch from the command line arguments
  -tf, --templateFile <value>
                           Calendar template file
  -if, --inputFolder <value>
                           Folder containing the images to process
  -of, --outputFolder <value>
                           Folder to write the calendars to
  -fw, --frameWidth <value>
                           Picture frame width
  -fh, --frameHeight <value>
                           Picture frame height
  -fox, --frameOffsetX <value>
                           Picture frame offset on X axis
  -foy, --frameOffsetY <value>
                           Picture frame offset on Y axis
  -p, --parallelism <value>
                           Number of calendars generated in parallel
````

### Configuration based mode

You can provide a json configuration file to describe a set of batches that you need to generate.

The configuration file must be defined like this:
```
{
  "batches": [
    {
      "templateFile": "./calendar_portrait_2021.png",
      "inputFolder": "./in/families",
      "outputFolder": "./out/families",
      "frameWidth": 2902,
      "frameHeight": 1919,
      "frameOffsetX": 263,
      "frameOffsetY": 201
    },
    {
      "templateFile": "./calendar_portrait_2021.png",
      "inputFolder": "./in/groups",
      "outputFolder": "./out/groups",
      "frameWidth": 2902,
      "frameHeight": 1919,
      "frameOffsetX": 263,
      "frameOffsetY": 201
    },
    {
      "templateFile": "./calendar_landscape_2021.png",
      "inputFolder": "./in/solos",
      "outputFolder": "./out/solos",
      "frameWidth": 1875,
      "frameHeight": 2817,
      "frameOffsetX": 137,
      "frameOffsetY": 156
    }
  ],
  "parallelism": 2
}
```
Each entry in the batches array represents a batch of calendars to generate.

The expected parameters are:
- `templateFile`: The path to the calendar layout
- `inputFolder`: The path to the folder containing the pictures to integrate in the layout
- `outputFolder`: The path to the folder which will contain the generated calendar
- `frameWidth`: The width of the area to place the picture
- `frameHeight`: The height of the area to place the picture
- `frameOffsetX`: The offset of the area to place the picture from the left side of the layout
- `frameOffsetY`: The offset of the area to place the picture from the top side of the layout

It is also possible to provide a `parallelism` parameter, which will define how many calendars will be generated in parallel. Depending on your computer changing this value might affect the time required to generate all the calendars and the performance of your computer in a good or a bad way. 

You can then run the program like this:
```
java -jar ./CalendarToolkit-assembly-1.0.jar generateFromConfig -c ./config.json
```

### Arguments based mode

The arguments based mode is only able to generate one batch at a time but can be used like this:
```
java -jar ./CalendarToolkit-assembly-1.0.jar generateFromArgs -tf ./calendar_portrait_2021.png -if ./in/families -of ./out/families -fw 2902 -fh 1919 -fox 201 -foy 263 -p 3
```

Each parameter is described in the Usage section and matches the values provided in the first batch of the Configuration based mode description.