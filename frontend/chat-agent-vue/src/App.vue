<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <div class="top-bar">
      <div class="avatar-wrapper">
        <!-- 顶部机器人头像（本地图片） -->
        <el-avatar size="medium" class="robot-avatar">
          <img src="/images/robot.png" alt="机器人头像" style="width: 100%; height: 100%;">
        </el-avatar>
        <span class="app-title">AgentScope 智能助手</span>
      </div>
    </div>

    <!-- 聊天区域 -->
    <div class="chat-area">
      <div class="messages-wrapper">
        <!-- 机器人消息（左） -->
        <div v-for="(msg, idx) in messages" :key="idx" class="msg-row" :class="msg.isUser ? 'user-row' : 'bot-row'">
          <!-- 机器人消息：头像在左（本地图片） -->
          <template v-if="!msg.isUser">
            <el-avatar class="msg-avatar bot-avatar">
              <img src="/images/robot.png" alt="机器人头像" style="width: 100%; height: 100%;">
            </el-avatar>
            <div class="msg-bubble bot-bubble">{{ msg.content }}</div>
          </template>
          <!-- 用户消息：头像在右（本地图片） -->
          <template v-else>
            <div class="msg-bubble user-bubble">{{ msg.content }}</div>
            <el-avatar class="msg-avatar user-avatar">
              <!-- 修正：图片后缀写错了，ipg → png -->
              <img src="/images/user.jpg" alt="用户头像" style="width: 100%; height: 100%;">
            </el-avatar>
          </template>
        </div>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <el-input
        v-model="inputText"
        placeholder="输入消息，按回车发送..."
        @keyup.enter="sendMsg"
        class="msg-input"
        size="large"  <!-- 输入框放大 -->
      >
        <template #append>
          <!-- 新增按钮组，实现按钮分开 -->
          <div class="btn-group">
            <el-button type="primary" @click="sendMsg" size="large">发送</el-button>
            <el-button type="danger" @click="resetChat" size="large">重置</el-button>
          </div>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const baseUrl = 'http://localhost:8080/api/agent'
const userId = 'test001'

const inputText = ref('')
const messages = ref([
  { content: '你好！我是基于 AgentScope + 通义千问的智能助手～', isUser: false }
])

const sendMsg = async () => {
  if (!inputText.value.trim()) return
  const userMsg = inputText.value
  messages.value.push({ content: userMsg, isUser: true })
  inputText.value = ''

  try {
    const res = await axios.post(`${baseUrl}/chat`, { userId, message: userMsg })
    messages.value.push({ content: res.data, isUser: false })
  } catch (err) {
    messages.value.push({ content: '服务暂时不可用', isUser: false })
    ElMessage.error('连接失败')
  }
}

const resetChat = async () => {
  await axios.post(`${baseUrl}/reset/${userId}`)
  messages.value = [{ content: '对话已重置！', isUser: false }]
}
</script>

<style scoped>
/* 全局容器（本地背景图+遮罩） */
.app-container {
  width: 100%;
  height: 100vh;
  display: flex;
  flex-direction: column;
  /* 本地背景图路径 */
  background: url('../public/images/bg.jpg') no-repeat center center;
  background-size: cover; /* 铺满屏幕 */
  position: relative; /* 配合遮罩层 */
}
/* 半透明遮罩（让文字更清晰，可调整透明度） */
.app-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.85); /* 0.85是透明度，0-1之间调整 */
  z-index: -1; /* 遮罩在背景图上面，内容下面 */
}

/* 顶部导航 */
.top-bar {
  background: rgba(255, 255, 255, 0.9); /* 半透明白色，适配背景 */
  padding: 12px 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.avatar-wrapper {
  display: flex;
  align-items: center;
}
.robot-avatar {
  margin-right: 12px;
}
.app-title {
  font-size: 20px; /* 标题文字放大（原18px） */
  font-weight: 600;
  color: #1e293b;
}

/* 聊天区域 */
.chat-area {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
.messages-wrapper {
  max-width: 900px;
  margin: 0 auto;
}

/* 消息行 */
.msg-row {
  display: flex;
  margin: 16px 0;
  align-items: flex-start;
  gap: 12px; /* 头像和气泡间距 */
}
.user-row {
  justify-content: flex-end;
}

/* 头像样式（固定尺寸） */
.msg-avatar {
  width: 40px;
  height: 40px;
  flex-shrink: 0;
  border: 2px solid #ffffff; /* 头像加白色边框，更美观 */
}
.bot-avatar {
  background: #f5f7fa; /* 兜底背景（图片加载失败时显示） */
}
.user-avatar {
  background: #f5f7fa; /* 兜底背景 */
}

/* 气泡（核心：文字放大） */
.msg-bubble {
  padding: 14px 18px; /* 内边距加大，文字不拥挤 */
  border-radius: 16px;
  max-width: 65%;
  line-height: 1.6; /* 行高加大，更易读 */
  font-size: 16px; /* 气泡文字放大（原14px） */
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}
.bot-bubble {
  background: rgba(255, 255, 255, 0.95); /* 半透明白色 */
  color: #1e293b;
  border-bottom-left-radius: 4px;
}
.user-bubble {
  background: rgba(59, 130, 246, 0.95); /* 半透明蓝色 */
  color: #ffffff;
  border-bottom-right-radius: 4px;
}

/* 输入区域 */
.input-area {
  padding: 16px 24px;
  background: rgba(255, 255, 255, 0.9); /* 半透明白色 */
  box-shadow: 0 -2px 8px rgba(0,0,0,0.08);
}
.msg-input {
  max-width: 900px;
  margin: 0 auto;
}

/* 新增：按钮组样式（实现按钮分开+间距） */
.btn-group {
  display: flex;
  gap: 12px; /* 按钮之间的间距 */
}
</style>