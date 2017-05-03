# Face Identification

## About project

This application allows you to recognize faces. The application allows you to add an image to the database (DB), as well as specify its name.
If the new image corresponds to images in the database by more than 80%, then the person is identified.

### Architecture specs

Here we've used [MVP](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter) pattern, which allows us to separate view elements from the logic and retrieving data.
In _View_ we contain only View elements, lists, specific Android parts which need to be displayed to user. Also we have reference on Presenter class.
In _Model_ we've implemented retrieving of data with specific methods which needs Context for perform.
In _Presenter_ we contain references on Model, View and UseCase classes. Here we implement all the logic that is required for concrete View.


### Used Technologies

* RxAndroid
https://github.com/ReactiveX/RxAndroid
* Realm database
https://github.com/realm/realm-java



### License

[Apache-2.0](https://github.com/eroy/faceIdentification/blob/master/LICENCE.txt)
