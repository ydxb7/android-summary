# Observe the lifecycle of fragment

如果在 Activity 的布局文件中声明 Fragment，将其作为 <fragment> 元素插入您的 Activity layout 中，或者通过将其添加到某个现有的 ViewGroup，利用应用代码将其插入布局。这个 Activity 和 Fragment 的 lifecycle 是这样的：

* Activity: `onCreate()`
* Fragment: `onAttach()`
* Fragment: `onCreateView()`
* Fragment: `onStart()`
* Activity: `onStart()`
* Activity: `onResume()`
* Fragment: `onResume()`
* Fragment: `onPause()`
* Activity: `onPause()`
* Fragment: `onStop()`
* Activity: `onStop()`
* Fragment: `onDestroy()`
* Fragment: `onDetach()`
* Activity: `onDestroy()`
* Activity: `onDetachedFromWindow()`

rotate:

* Fragment: `onPause()`
* Activity: `onPause()`
* Fragment: `onStop()`
* Activity: `onStop()`
* Fragment: `onDestroy()`
* Fragment: `onDetach()`
* Activity: `onDestroy()`
* Activity: `onDetachedFromWindow()`
* Activity: `onCreate()`
* Fragment: `onAttach()`
* Fragment: `onCreateView()`
* Fragment: `onStart()`
* Activity: `onStart()`
* Activity: `onResume()`
* Fragment: `onResume()`
