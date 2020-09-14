![演示](https://github.com/wongkyunban/AndroidSignDemo/raw/master/3h1zf-ea0ng.gif)
<img src="https://github.com/wongkyunban/AndroidSignDemo/raw/master/3h1zf-ea0ng.gif"/>
# Usage
## Java Code
```java
        LinearLayout linearLayout = findViewById(R.id.ll_sign_panel);
        tuya = new HandwritingBoardView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tuya.setLayoutParams(params);
        linearLayout.addView(tuya);
```

## XML
```xml
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <com.wong.sign.HandwritingBoardView
        android:orientation="vertical"
        android:id="@+id/ll_sign_panel"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:text="Hello World!"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>

```
