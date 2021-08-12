# LoadApp

[![Expected Result](https://img.youtube.com/vi/a2l2cuMWh20/0.jpg)](https://www.youtube.com/watch?v=a2l2cuMWh20)

## Checklist

### Project Instructions
- [x] Create a radio list of the following options where one of them can be selected for downloading:
  * https://github.com/bumptech/glide
  * https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter
  * https://github.com/square/retrofit
- [x] Create a custom loading button by extending View class and assigning custom attributes to it
- [x] Animate properties of the custom button once it’s clicked
- [x] Add the custom button to the main screen, set on click listener and call download() function with selected Url
- [x] If there is no selected option, display a Toast to let the user know to select one.
- [x] Once the download is complete, send a notification with custom style and design
- [x] Add a button with action to the notification, that opens a detailed screen of a downloaded repository
- [x] Create the details screen and display the name of the repository and status of the download
- [ ] Use declarative XML with motionLayout to coordinate animations across the views on the detail screen
- [x] Add a button to the detail screen to return back to the main screen.

### Project Rubric
- [ ] Code compiles and runs without errors. The app should be cloned from Github, opened in local Android Studio and run on a device.
- [x] Customize and display of information using canvas with desired color and style:
  - [x] A custom button is created by extending View and custom attributes like background and text colors, etc are assigned to it.
  - [x] At least 2 custom attributes like background and text colors, etc are assigned to the button.
    - [x] Text and background are drawn using canvas
- [x] Animate UI elements with property animations to provide users with visual feedback in an Android app. The custom button properties like background, text and additional circle are animated by changing the width, text, and color
- [x] Send contextual messages using notifications to keep users informed:
  - [x] At least 2 types of contextual messages are displayed to the user: toast and notification
    - [x] The toast is displayed inside the app
    - [x] The notification is created and displayed in the status bar
- [x] Add custom functionality to the notifications. A button is added to the notification which opens in a separate screen and custom values are passed to it
- [ ] Use declarative XML with MotionLayout to coordinate animations across multiple views. MotionLayout is used to enhance the user experience when switching activities

### Extra
- [ ] Handle the animation if downloading/uploading takes a longer or shorter time than animation in the custom button [We don’t know how fast is the download, so once it’s complete make a function that cancels current animation and starts it over with different duration]
- [ ] Add an additional view(EditText) in MainActivity where users can enter custom URLs for downloading/uploading files [Make sure to check if the inputted value is a valid url]
- [ ] Open the downloaded file and display the information to the user on DetailsActivity
- [ ] Customize notification UI based on the status of the download/upload (progress, fail, success)