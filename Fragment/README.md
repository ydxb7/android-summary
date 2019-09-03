# Observe the lifecycle of fragment

如果在 Activity 的布局文件中声明 Fragment，将其作为 `<fragment>` 元素插入您的 Activity layout 中，或者通过将其添加到某个现有的 ViewGroup，利用应用代码将其插入布局。这个 Activity 和 Fragment 的 lifecycle 是这样的：
layout 中 `<fragment>` 和 `<FrameLayout>`（在Activity的`onCreate()`中Transaction） 这两种情况的lifecycle是一样的。

![image](activity_fragment_lifecycle.png)

* Activity: `onCreate()`
* Fragment: `onAttach()`
* Fragment: `onCreate()`
* Fragment: `onCreateView()`
* Fragment: `onActivityCreated()`
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
* Fragment: `onCreate()`
* Fragment: `onCreateView()`
* Fragment: `onActivityCreated()`
* Fragment: `onStart()`
* Activity: `onStart()`
* Activity: `onResume()`
* Fragment: `onResume()`

## 与 Activity 生命周期协调一致
Fragment 所在 Activity 的生命周期会直接影响Fragment的生命周期，其表现为，Activity 的每次生命周期回调都会引发每个Fragment的类似回调。例如，当 Activity 收到 onPause() 时，Activity 中的每个片段也会收到 onPause()。

不过，Fragment还有几个额外的生命周期回调，用于处理与 Activity 的唯一交互，从而执行构建和销毁Fragment界面等操作。这些额外的回调方法是：

* `onAttach()`
在 Fragment 已与 Activity 关联时进行调用（Activity 传递到此方法内）。

* `onCreateView()`
调用它可创建与Fragment关联的视图层次结构。

* `onActivityCreated()`
当 Activity 的 onCreate() 方法已返回时进行调用。

* `onDestroyView()`
在移除与Fragment关联的视图层次结构时进行调用。

* `onDetach()`
在取消片段与 Activity 的关联时进行调用。

图片所示为受宿主 Activity 影响的片段生命周期流。在该图中，您可以看到 Activity 的每个连续状态如何确定片段可收到的回调方法。例如，**当 Activity 收到其 onCreate() 回调时，Activity 中的片段只会收到 onActivityCreated() 回调。**

一旦 Activity 达到已恢复状态，您便可随意向 Activity 添加Fragment和移除其中的Fragment。因此，**只有当 Activity 处于已恢复状态时，片段的生命周期才能独立变化。**

## Example
其中的 Activity 使用两个Fragment来创建一个双窗格布局。下面的 Activity 包括两个Fragment：一个用于显示莎士比亚戏剧标题列表，另一个用于在从列表中选定戏剧时显示其摘要。此外，它还展示了如何根据屏幕配置提供不同的Fragment配置。

![image](show_fragment.gif)

layout 中 一个 TitlesFragment 用 `<fragment>`, DetailsFragment 用 `<FrameLayout>`, 在 TitlesFragment 的 `onActivityCreated()` 中判断有没有显示 DetailsFragment (是否有2个Pane)，并给出相应的操作。

```
public static class TitlesFragment extends ListFragment {
    boolean dualPane;
    int curCheckPosition = 0;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Populate list with our static array of titles.
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_activated_1, Shakespeare.TITLES));

        // Check to see if we have a frame in which to embed the details
        // fragment directly in the containing UI.
        View detailsFrame = getActivity().findViewById(R.id.details);
        dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (savedInstanceState != null) {
            // Restore last state for checked position.
            curCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }

        if (dualPane) {
            // In dual-pane mode, the list view highlights the selected item.
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            // Make sure our UI is in the correct state.
            showDetails(curCheckPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", curCheckPosition);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        showDetails(position);
    }

    /**
     * Helper function to show the details of a selected item, either by
     * displaying a fragment in-place in the current UI, or starting a
     * whole new activity in which it is displayed.
     */
    void showDetails(int index) {
        curCheckPosition = index;

        if (dualPane) {
            // We can display everything in-place with fragments, so update
            // the list to highlight the selected item and show the data.
            getListView().setItemChecked(index, true);

            // Check what fragment is currently shown, replace if needed.
            DetailsFragment details = (DetailsFragment)
                    getFragmentManager().findFragmentById(R.id.details);
            if (details == null || details.getShownIndex() != index) {
                // Make new fragment to show this selection.
                details = DetailsFragment.newInstance(index);

                // Execute a transaction, replacing any existing fragment
                // with this one inside the frame.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                if (index == 0) {
                    ft.replace(R.id.details, details);
                } else {
                    ft.replace(R.id.details, details);
                }
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        } else {
            // Otherwise we need to launch a new activity to display
            // the dialog fragment with selected text.
            Intent intent = new Intent();
            intent.setClass(getActivity(), DetailsActivity.class);
            intent.putExtra("index", index);
            startActivity(intent);
        }
    }
}
```

