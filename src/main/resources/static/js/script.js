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