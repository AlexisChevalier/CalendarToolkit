# CalendarToolkit
Simple command line tools used to help generate and make calendars for a local sports association


# Usage

````
Calendar Toolkit
Usage: calendarToolkit [layout|generate]

Command: layout [options]
Prints the calendar tabbed layout for indesign
  -y, --year <value>       Year to generate the layout for
  
Command: generate [options]
Generates the calendar files
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
  -ng, --numberOfGenerators <value>
                           Number of parallel generators
````