function buttonEdit() {
    document.querySelector('button.edit').style.display = 'none';
    document.querySelector('button.save').style.display = 'inline';
    document.querySelector('button.cancel').style.display = 'inline';
    document.querySelectorAll('div.this').forEach(el => {
        el.style.display = 'none';
    });
    document.querySelectorAll('div.edit').forEach(el => {
        el.style.display = 'inline';
    });
    document.querySelectorAll('span.password').forEach(el => {
        el.style.display = 'inline';
    });

}

function buttonSaveOrCancel() {
    document.querySelector('button.edit').style.display = 'inline';
    document.querySelector('button.save').style.display = 'none';
    document.querySelector('button.cancel').style.display = 'none';
    document.querySelectorAll('div.this').forEach(el => {
        el.style.display = 'inline';
    });
    document.querySelectorAll('div.edit').forEach(el => {
        el.style.display = 'none';
    });
    document.querySelectorAll('span.password').forEach(el => {
        el.style.display = 'none';
    });

}

function tabs(activeTab) {

    document.querySelector('a.tabs.reports').style.backgroundColor = 'none';
    document.querySelector('a.tabs.reports').style.backgroundColor = 'white';

    document.querySelector('a.tabs.auth').style.backgroundColor = 'none';
    document.querySelector('a.tabs.auth').style.backgroundColor = 'white';

    document.querySelector('a.tabs.users').style.backgroundColor = 'none';
    document.querySelector('a.tabs.users').style.backgroundColor = 'white';

    function tabs(activeTab) {
        document.querySelectorAll('a.tabs').forEach(tab => {
            tab.classList.remove('active');
        });

        const selectedTab = document.querySelector(`a.tabs.${activeTab}`);
        if (selectedTab) {
            selectedTab.classList.add('active');
        }
    }
}

function create() {
    const input = document.getElementById('fileInput');
    const drop = document.getElementById('createDropPane');
    const loaded = document.getElementById('createLoadedPane');
    const label = document.getElementById('createdFileLabel');
    if (!input || !drop || !loaded) return;
    const file = input.files?.[0];
    const ok = !!file?.name;
    drop.style.display = ok ? 'none' : 'flex';
    loaded.style.display = ok ? 'flex' : 'none';
    if (label && ok) label.textContent = file.name;
}

document.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById('fileInput');
    const zone = document.querySelector('.drop-zone');
    if (!input || !zone) return;

    const stop = (e) => { e.preventDefault(); e.stopPropagation(); };
    ['dragenter', 'dragover', 'dragleave'].forEach((ev) => zone.addEventListener(ev, stop));
    zone.addEventListener('drop', (e) => {
        stop(e);
        const file = e.dataTransfer?.files?.[0];
        if (!file) return;
        const dt = new DataTransfer();
        dt.items.add(file);
        input.files = dt.files;
        input.dispatchEvent(new Event('change', { bubbles: true }));
    });

    input.addEventListener('change', create);
    create();
});