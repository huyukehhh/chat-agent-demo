import { createApp } from 'vue'
import App from './App.vue'
// 全局引入Element Plus组件库和样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 创建Vue应用并挂载
const app = createApp(App)
app.use(ElementPlus) // 注册Element Plus
app.mount('#app')