# DebugTools
这是一个Debug的应用内工具集，目前还没开始开发，先立项，创建一个文件夹。

* `lite` 只包含接口，用于持久的集成到应用中，减少正式包的体积。
* `core` 只包含数据，需要 `lite` 的支持，提供 `lite` 的接口实现，但是只提供数据的输入输出接口。用于需要在正式包中记录数据的场景。
* `local` 只包含本地UI，需要 `core` 与 `lite` 的支撑，只包含交互UI，一般用于 `Debug` 包下的调试入口。
* `remote` 本机的独立调试应用，需要 `core` 的支撑，用于直接连接并挂载集成了 `core` 的正式包。
* `desktop` 基于局域网的桌面端操作客户端，需要 `remote` 的支持，用于在电脑桌面操作或检索数据。