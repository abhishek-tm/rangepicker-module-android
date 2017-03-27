# Material Range Picker
It is a simple range picker can be used in most common scenarios where you've to pick the range in hours. It has some presets for the hours and days like _last 12 hours to now_, _today_, _yesterday_ etc. There's no 'android.widget.ListView' or 'android.support.v7.widget.RecyclerView'. All the elements have been put in xml manually. So the layout is free to edit according to UI/theme of the application.

If you really dont want to waste time on making a simple time range picker, this is good for you. In this version, there's not too much customizations in this library/time picker screen. But in future, customizations can be done according response of people.
<div>
<img src="/wiki/preset.png" width="280" alt="Presets"/>
<img src="/wiki/custom.png" width="280" alt="Custom Time Selection"/>
<img src="/wiki/calendar.png" width="280" alt="Custom Date Selection"/>
</div>


### How to add library?

Simply add the following repositories to your project level `build.gradle` file:

```groovy
allprojects {
    repositories {
        jcenter()

        maven {
            url = 'https://jitpack.io'
        }
    }
}
```

And add the following dependency to your app level `build.gradle` file:
```groovy
dependencies {
    compile 'com.github.abhishek-tm:rangepicker-module-android:1.0.1'
}
```

### How to use it?
This range picker is an 'Activity for Results'. So simply launch it with a response code like below...

```java
startActivityForResult(new Intent(this, TimePicker.class), 1992);
```
And obtain the results in 'onActivityResult()' as following...

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == Activity.RESULT_OK && requestCode == 1992) {
        Calendar from = (Calendar) data.getSerializableExtra(TimePicker.FROM);
        Calendar to = (Calendar) data.getSerializableExtra(TimePicker.TO);
    }
}
```
That's all! :wink:

### License
Copyright (C) 2017  Teramatrix Technologies Private Limited

> This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.<br/><br/>
> This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.<br/><br/>
> You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
