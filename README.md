# EasyTodo

## 15软工三人组开发
- 主力: **Wendy**
- 划水: **Sdite**, **Mxin**

## 历史

- **17/10/29**
    + 保留安卓长按删除的菜单
    + 更改分享的图标
    + 解决拖动后没有更新到数据库的问题(给数据库加多一个pos属性，每次移动后更改pos，Item显示的时候根据pos的顺序来显示即可)
    + 删除多余的代码， 并整合修改代码，小整理
    + 修复过期显示bug
    + 搞定部分小细节的bug， 比如修改事件的名称判空，限制一行等问题

- **17/10/28**
    + 10/26做的事件滑动删除由于无法用ItemTouchHelper来实现滑动菜单，前功尽弃，**抛弃了该方法**
    + 采用外援库[**SwipeRecyclerView**](https://github.com/yanzhenjie/SwipeRecyclerView)实现事件项的拖拽交换， 滑动菜单

- **17/10/26**
    + 添加事件的EditText 禁止换行，且对软键盘的确定按钮进行响应，并判断EditText内容是否为空
    + 完成事件项 的拖动交换， 滑动删除的功能

- **17/10/25**
    + 修改添加事件的button的响应，使其点击时隐藏，添加完按钮后显示
    + OptionMenu中的全部删除的点击事件加入AlertDialog，防止用户错误点击致使所有事件被删除
    + 添加事件时EditText会获取焦点并自动弹出键盘

- **17/10/25前** 
    + 完成EasyTodo所有基本功能