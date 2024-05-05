
# Naraka

**Naraka**音译自佛经梵文नरक，佛经中形容永不能解脱的无间地狱。每一个App死掉(崩溃)后，都应该进入地狱(Naraka)。



## 安装

步骤 1. 将 JitPack 存储库添加到 settings.gradle 文件中

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```


步骤 2. 将 Naraka 依赖项添加到您的 build.gradle(app) 文件中。

```groovy
dependencies {
    implementation 'com.github.Vinx911:Naraka:1.0.0'
}
```



## 用法

初始化Naraka

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // 使用默认的错误界面
        // Naraka.initialize(this, NarakaDefaultErrorActivity::class.java)
        
        // 使用自定义的错误界面
        // Naraka.initialize(this, CrashActivity::class.java)
        
        // 使用自定义的错误界面, 并且处理错误回调，在此处可实现错误上传或者保存操作
        Naraka.initialize(this, CrashActivity::class.java) {
            Log.e("TAG", "onCreate: $it")
        }
    }
}
```

自定义错误界面

```kotlin
class CrashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         Naraka.getCrashMsgFromIntent(intent).let { msg ->
            // 错误显示等
        }
        setContentView(view)
    }
}
```



## 功能

### 初始化Naraka

```kotlin
fun initialize(
	context: Context,
    errorActivity: Class<out Activity>? = null,
    errorOccurredListener: ErrorOccurredListener? = null
)
```

| Parameter 范围        | Type 类型              | Description 描述          |
| --------------------- | ---------------------- | ------------------------- |
| `context`             | `Context`              | 启动 Intent时需要         |
| `errorActivity`       | `Activity`             | 应用崩溃时启动的 Activity |
| errorOccurredListener | ErrorOccurredListener? | 错误发生时的回调          |



### 关闭App

```kotlin
fun closeApplication(activity: Activity)
```

| Parameter 范围 | Type 类型  | Description 描述                              |
| -------------- | ---------- | --------------------------------------------- |
| `activity`     | `Activity` | 执行此操作所在的 Activity，用于关闭此Activity |



### 重启App

```kotlin
fun restartApplication(activity: Activity)
```

| Parameter 范围 | Type 类型  | Description 描述                              |
| -------------- | ---------- | --------------------------------------------- |
| `activity`     | `Activity` | 执行此操作所在的 Activity，用于关闭此Activity |



### 从Intent中获取StackTrace

在错误Activity中调用

```kotlin
fun getStackTraceFromIntent(intent: Intent): String
```

| Parameter 范围 | Type 类型 | Description 描述     |
| -------------- | --------- | -------------------- |
| `intent`       | `Intent`  | 包含了错误的一些信息 |



### 从Intent中获取崩溃消息

在错误Activity中调用

```kotlin
fun getCrashMsgFromIntent(intent: Intent): String
```

| Parameter 范围 | Type 类型 | Description 描述     |
| -------------- | --------- | -------------------- |
| `intent`       | `Intent`  | 包含了错误的一些信息 |



### 从Intent中获取错误的详细信息

包括时间，编译版本、编译时间、当前日期、设备信息、安卓版本、堆栈轨迹等，在错误Activity中调用。

```kotlin
fun getErrorDetailsFromIntent(intent: Intent): String
```

| Parameter 范围 | Type 类型 | Description 描述     |
| -------------- | --------- | -------------------- |
| `intent`       | `Intent`  | 包含了错误的一些信息 |



## 截图

![screenshot](./screenshot.gif)
