## 即食智能订餐系统服务端
### 项目介绍
本项目不仅仅是一个传统的订餐系统，更是一个融合了 RAG（检索增强生成） 与 Agent（智能体） 技术的 AI 驱动型应用。它实现了从“用户模糊意图”到“精准商品推荐”再到“实时库存确认”的全自动化闭环，重新定义了人机交互的点餐体验。
### 项目亮点
1. AI 驱动的双层智能检索架构 (核心创新) \
摒弃了传统的关键词匹配，设计了 “语义理解 + 业务校验” 的串联工作流：\
- 第一层：语义召回 (Qdrant RAG) \
利用 Qdrant 向量数据库存储菜品特征向量。
当用户输入“想吃点辣的”或“适合减肥吃的”时，系统通过向量相似度计算，精准召回符合语义的候选菜品集合，解决自然语言理解的模糊性难题。
- 第二层：实时校验 (Redis Agent Tool)\
基于 LangChain4j Function Calling 机制，AI 自动调用自定义 Redis 工具。
对召回的候选菜品进行实时库存、价格、售卖状态核查，自动过滤售罄商品，确保推荐结果“不仅懂你，而且能买”。
2. 流式交互与结构化卡片渲染
- SSE (Server-Sent Events)：后端采用 SSE 技术推送 AI 生成的流式回复，实现类似 ChatGPT 的“打字机”效果，降低用户等待焦虑。
- 智能卡片解析：约定前后端数据协议，AI 在输出自然语言的同时，动态生成结构化 JSON 数据。前端自动解析并渲染为可点击的商品卡片，用户点击即可一键加入购物车，实现“对话即点餐”。
3. 企业级多模块工程架构
- 采用 Maven Multi-Module 结构，严格遵循高内聚、低耦合原则：swiftbite-common, swiftbite-pojo, swiftbite-server
### 功能展示
![img.png](imgs/img.png)
![img_1.png](imgs/img_1.png)
### 运行
1. 克隆项目
    ```bash
    git clone https://github.com/yourname/swiftbite-backend.git
    ```
2. 配置application.yml
3. 更新Maven库
4. 启动项目

