const API_BASE = 'http://localhost:8089/api';
const token = localStorage.getItem('token');
const userId = localStorage.getItem('userId');
const nickname = localStorage.getItem('nickname');

// 检查登录状态
if (!token) {
    window.location.href = '/login.html';
}

// 显示用户昵称
document.getElementById('user-nickname').textContent = nickname || '用户';

// 通用API调用函数
async function apiCall(endpoint, method = 'GET', data = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    };

    if (data) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(API_BASE + endpoint, options);
        const result = await response.json();

        if (response.status === 401) {
            alert('登录已过期，请重新登录');
            logout();
            return null;
        }

        return result;
    } catch (error) {
        console.error('API调用失败:', error);
        return { code: 500, message: '网络错误' };
    }
}

// 切换面板
function switchPanel(panelName) {
    document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.panel').forEach(p => p.classList.remove('active'));

    event.target.classList.add('active');
    document.getElementById(panelName + '-panel').classList.add('active');
}

// 退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userId');
    localStorage.removeItem('nickname');
    window.location.href = '/login.html';
}

// 获取聊天建议
async function getSuggestions(e) {
    e.preventDefault();

    const btn = document.getElementById('suggest-btn');
    const resultDiv = document.getElementById('suggest-result');

    btn.disabled = true;
    btn.textContent = 'AI思考中...';
    resultDiv.innerHTML = '<div class="loading">正在生成建议...</div>';

    const message = document.getElementById('suggest-message').value;
    const data = {
        scene: document.getElementById('suggest-scene').value,
        gender: document.getElementById('suggest-gender').value,
        relationship: document.getElementById('suggest-relationship').value,
        messages: [{ role: 'other', content: message }]
    };

    const result = await apiCall('/chat/suggest', 'POST', data);

    btn.disabled = false;
    btn.textContent = '获取建议';

    if (result && result.code === 200) {
        const suggestions = result.data.suggestions || [];
        const emotion = result.data.emotion || {};

        let html = '<div class="result-box"><h3>💡 回复建议</h3>';

        suggestions.forEach((item, index) => {
            html += `
                <div class="suggestion-item">
                    <span class="tone">${item.tone || '建议' + (index + 1)}</span>
                    <div class="text">${item.text}</div>
                </div>
            `;
        });

        if (emotion.type) {
            html += `
                <div class="emotion-box">
                    <strong>情绪分析：</strong>${emotion.description || emotion.type}
                    （强度：${emotion.intensity || 0.5}）
                </div>
            `;
        }

        html += '</div>';
        resultDiv.innerHTML = html;
    } else {
        resultDiv.innerHTML = `<div class="result-box" style="border-left-color: #f44336;">
            <h3 style="color: #f44336;">❌ 错误</h3>
            <p>${result?.message || '获取建议失败'}</p>
        </div>`;
    }
}

// 对话分析
async function analyzeChat(e) {
    e.preventDefault();

    const btn = document.getElementById('analyze-btn');
    const resultDiv = document.getElementById('analyze-result');

    btn.disabled = true;
    btn.textContent = 'AI分析中...';
    resultDiv.innerHTML = '<div class="loading">正在分析对话...</div>';

    const text = document.getElementById('analyze-conversation').value;
    const lines = text.split('\n').filter(line => line.trim());

    const conversation = lines.map(line => {
        if (line.startsWith('我:') || line.startsWith('我：')) {
            return { role: 'user', content: line.substring(2).trim() };
        } else if (line.startsWith('对方:') || line.startsWith('对方：')) {
            return { role: 'other', content: line.substring(3).trim() };
        }
        return null;
    }).filter(item => item !== null);

    if (conversation.length === 0) {
        resultDiv.innerHTML = `<div class="result-box" style="border-left-color: #f44336;">
            <p>请按格式输入对话：我: 消息内容 或 对方: 消息内容</p>
        </div>`;
        btn.disabled = false;
        btn.textContent = '分析对话';
        return;
    }

    const result = await apiCall('/chat/analyze', 'POST', { conversation });

    btn.disabled = false;
    btn.textContent = '分析对话';

    if (result && result.code === 200) {
        const data = result.data;
        let html = '<div class="result-box"><h3>📊 分析结果</h3>';

        html += `<p><strong>情绪走向：</strong>${data.emotionTrend || '未知'}</p>`;
        html += `<p><strong>沟通质量：</strong>${data.qualityScore || 0}分</p>`;

        if (data.problems && data.problems.length > 0) {
            html += '<p><strong>发现的问题：</strong></p><ul>';
            data.problems.forEach(p => html += `<li>${p}</li>`);
            html += '</ul>';
        }

        if (data.suggestions && data.suggestions.length > 0) {
            html += '<p><strong>改进建议：</strong></p><ul>';
            data.suggestions.forEach(s => html += `<li>${s}</li>`);
            html += '</ul>';
        }

        html += '</div>';
        resultDiv.innerHTML = html;
    } else {
        resultDiv.innerHTML = `<div class="result-box" style="border-left-color: #f44336;">
            <h3 style="color: #f44336;">❌ 错误</h3>
            <p>${result?.message || '分析失败'}</p>
        </div>`;
    }
}

// 情境模拟
async function simulate(e) {
    e.preventDefault();

    const btn = document.getElementById('sim-btn');
    const resultDiv = document.getElementById('sim-result');

    btn.disabled = true;
    btn.textContent = 'AI回复中...';
    resultDiv.innerHTML = '<div class="loading">AI正在扮演对方...</div>';

    const data = {
        scenario: document.getElementById('sim-scenario').value,
        targetGender: document.getElementById('sim-gender').value,
        relationship: 'dating',
        userMessage: document.getElementById('sim-message').value
    };

    const result = await apiCall('/chat/simulate', 'POST', data);

    btn.disabled = false;
    btn.textContent = '发送';

    if (result && result.code === 200) {
        const data = result.data;
        let html = '<div class="result-box"><h3>💬 对方的回复</h3>';
        html += `<div class="suggestion-item"><div class="text">${data.reply}</div></div>`;

        if (data.feedback) {
            html += `<div class="emotion-box">
                <strong>💡 反馈：</strong>${data.feedback}
                ${data.score ? `（评分：${data.score}分）` : ''}
            </div>`;
        }

        html += '</div>';
        resultDiv.innerHTML = html;
    } else {
        resultDiv.innerHTML = `<div class="result-box" style="border-left-color: #f44336;">
            <h3 style="color: #f44336;">❌ 错误</h3>
            <p>${result?.message || '模拟失败'}</p>
        </div>`;
    }
}

// 创建档案
async function createProfile(e) {
    e.preventDefault();

    const btn = document.getElementById('profile-btn');
    const resultDiv = document.getElementById('profile-result');

    btn.disabled = true;
    btn.textContent = '创建中...';

    const data = {
        nickname: document.getElementById('profile-nickname').value,
        gender: document.getElementById('profile-gender').value
    };

    const result = await apiCall('/target/create', 'POST', data);

    btn.disabled = false;
    btn.textContent = '创建档案';

    if (result && result.code === 200) {
        resultDiv.innerHTML = `<div class="result-box">
            <h3 style="color: #4caf50;">✅ 创建成功</h3>
            <p>档案「${data.nickname}」已创建</p>
        </div>`;

        document.getElementById('profile-nickname').value = '';
        loadProfiles();
    } else {
        resultDiv.innerHTML = `<div class="result-box" style="border-left-color: #f44336;">
            <h3 style="color: #f44336;">❌ 错误</h3>
            <p>${result?.message || '创建失败'}</p>
        </div>`;
    }
}

// 加载档案列表
async function loadProfiles() {
    const listDiv = document.getElementById('profile-list');
    listDiv.innerHTML = '<div class="loading">加载中...</div>';

    const result = await apiCall('/target/list', 'GET');

    if (result && result.code === 200) {
        const profiles = result.data || [];

        if (profiles.length === 0) {
            listDiv.innerHTML = '<p style="text-align:center; color:#999; padding:20px;">暂无档案</p>';
            return;
        }

        let html = '';
        profiles.forEach(profile => {
            html += `
                <div class="profile-card">
                    <h4>${profile.nickname}</h4>
                    <p>性别：${profile.gender === 'female' ? '女' : '男'}</p>
                    <p>关系：${profile.relationshipStatus || '未设置'}</p>
                    <p style="font-size:12px; color:#999;">创建于：${profile.createdAt || ''}</p>
                </div>
            `;
        });

        listDiv.innerHTML = html;
    } else {
        listDiv.innerHTML = '<p style="text-align:center; color:#f44336;">加载失败</p>';
    }
}