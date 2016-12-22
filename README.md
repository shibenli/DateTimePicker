# 这是一个从android源码改编过来的时间控件
[![](https://jitpack.io/v/shibenli/DateTimePicker.svg)](https://jitpack.io/#shibenli/DateTimePicker)最新版本请参考此处

这是一个从Android源码导出来的NumberPicker的项目，里面添加了一些常用的实现。

![image](Screenshot/Screenshot_2016-12-22-11-50-25.png)

### gradle
Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.shibenli:DateTimePicker:v0.0.8'
	}
