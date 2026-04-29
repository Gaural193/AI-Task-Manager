const API_BASE = '/tasks';

document.addEventListener('DOMContentLoaded', () => {
    fetchTasks();

    document.getElementById('ai-form').addEventListener('submit', handleAiSubmit);
    document.getElementById('task-form').addEventListener('submit', handleManualSubmit);
});

function switchTab(tab) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.form-container').forEach(container => container.classList.add('hidden'));

    if (tab === 'ai') {
        document.querySelector('.tab-btn:nth-child(1)').classList.add('active');
        document.getElementById('ai-form-container').classList.remove('hidden');
    } else {
        document.querySelector('.tab-btn:nth-child(2)').classList.add('active');
        document.getElementById('manual-form-container').classList.remove('hidden');
    }
}

async function handleAiSubmit(e) {
    e.preventDefault();
    const promptInput = document.getElementById('ai-prompt');
    const prompt = promptInput.value;
    const submitBtn = document.getElementById('ai-submit-btn');
    const btnText = submitBtn.querySelector('.btn-text');
    const spinner = submitBtn.querySelector('.spinner');
    
    btnText.textContent = 'Generating...';
    spinner.classList.remove('hidden');
    submitBtn.disabled = true;

    try {
        const response = await fetch(`${API_BASE}/suggest`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ prompt })
        });

        if (!response.ok) throw new Error('Failed to generate task');

        const suggestedTask = await response.json();
        
        // Switch to manual tab and populate form
        switchTab('manual');
        populateForm(suggestedTask);
        
        // Show success message
        const resultBox = document.getElementById('ai-result');
        resultBox.classList.remove('hidden');
        setTimeout(() => resultBox.classList.add('hidden'), 5000);
        
        promptInput.value = '';
    } catch (error) {
        alert('Error communicating with AI: ' + error.message);
    } finally {
        btnText.textContent = 'Generate Task';
        spinner.classList.add('hidden');
        submitBtn.disabled = false;
    }
}

async function handleManualSubmit(e) {
    e.preventDefault();
    
    const id = document.getElementById('task-id').value;
    const task = {
        title: document.getElementById('title').value,
        description: document.getElementById('description').value,
        dueDate: document.getElementById('dueDate').value,
        priority: document.getElementById('priority').value,
        status: document.getElementById('status').value
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_BASE}/${id}` : API_BASE;

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(task)
        });

        if (!response.ok) throw new Error('Failed to save task');

        resetForm();
        fetchTasks();
    } catch (error) {
        alert('Error saving task: ' + error.message);
    }
}

function populateForm(task) {
    document.getElementById('task-id').value = task.id || '';
    document.getElementById('title').value = task.title || '';
    document.getElementById('description').value = task.description || '';
    document.getElementById('dueDate').value = task.dueDate || '';
    document.getElementById('priority').value = task.priority || 'MEDIUM';
    document.getElementById('status').value = task.status || 'TODO';
}

function resetForm() {
    document.getElementById('task-id').value = '';
    document.getElementById('task-form').reset();
    document.getElementById('priority').value = 'MEDIUM';
    document.getElementById('status').value = 'TODO';
}

async function fetchTasks() {
    try {
        const response = await fetch(API_BASE);
        if (!response.ok) throw new Error('Failed to fetch tasks');
        const tasks = await response.json();
        renderTasks(tasks);
    } catch (error) {
        console.error('Error fetching tasks:', error);
    }
}

function renderTasks(tasks) {
    const todoList = document.getElementById('list-TODO');
    const inProgressList = document.getElementById('list-IN_PROGRESS');
    const doneList = document.getElementById('list-DONE');

    todoList.innerHTML = '';
    inProgressList.innerHTML = '';
    doneList.innerHTML = '';

    tasks.forEach(task => {
        const card = document.createElement('div');
        card.className = 'task-card';
        card.innerHTML = `
            <div class="task-header">
                <div class="task-title">${escapeHtml(task.title)}</div>
                <div class="task-priority priority-${task.priority}">${task.priority}</div>
            </div>
            <div class="task-desc">${escapeHtml(task.description || '')}</div>
            <div class="task-footer">
                <div class="task-date">${task.dueDate ? `📅 ${task.dueDate}` : 'No date'}</div>
                <div class="task-actions">
                    <button onclick='editTask(${JSON.stringify(task).replace(/'/g, "\\'")})'>✎ Edit</button>
                    <button class="delete-btn" onclick="deleteTask(${task.id})">🗑 Delete</button>
                </div>
            </div>
        `;

        if (task.status === 'TODO') todoList.appendChild(card);
        else if (task.status === 'IN_PROGRESS') inProgressList.appendChild(card);
        else doneList.appendChild(card);
    });
}

function editTask(task) {
    switchTab('manual');
    populateForm(task);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

async function deleteTask(id) {
    if (!confirm('Are you sure you want to delete this task?')) return;

    try {
        const response = await fetch(`${API_BASE}/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Failed to delete task');
        fetchTasks();
    } catch (error) {
        alert('Error deleting task: ' + error.message);
    }
}

function escapeHtml(unsafe) {
    if (!unsafe) return '';
    return unsafe
         .replace(/&/g, "&amp;")
         .replace(/</g, "&lt;")
         .replace(/>/g, "&gt;")
         .replace(/"/g, "&quot;")
         .replace(/'/g, "&#039;");
}
