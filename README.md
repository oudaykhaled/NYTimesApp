# NYTimesApp
A simple app to hit the NY Times Most Popular Articles API and show a list of articles, that shows details when items on the list are tapped (a typical master/detail app). We'll be using the most viewed section of this API. http://api.nytimes.com/svc/mostpopular/v2/mostviewed/{section}/{period}.json?apikey= sample-key.
You will notice that the project is devided into modules called "Area". Each area represent a section (Business section) in the App.
While the pattern used to represent the data on the UI is the Module-View-Presenter MVP.
The service locator design pattern is also used int this application to encapsulate the processes involved in obtaining a service with a strong abstraction layer. This pattern uses a central registry known as the "service locator", which on request returns the information necessary to perform a certain task. We have to mention here that this pattern can replace later on by Dagger 2 which is more powerfull in terms of performance.
Both the HttpManager and ImageLoader classes are wraped into 2 classes. Currently we are using Glide for Image Lazy Loading and Retrofit to manage the API calls, since they are used in the app through an abstract layer
they losley coupled with the application classes and can be easaly replace later on.
We are using the concept of One Activity - Multiple fragments which highly recommended by Google.
Currently we are using Proguard, we have to notice here that Dexguard is more secure in case the application hold sensitive data (Banking Solution for example).
Custom Modules are built inside the solution (app_repository & retrofit-http-manager) which can later on switched to an Artifactory Server.
For the unit testing we are using Mockito on to of JUnit.

