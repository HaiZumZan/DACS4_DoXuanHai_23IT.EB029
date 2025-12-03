const { app, BrowserWindow, session, desktopCapturer } = require('electron');
const path = require('path');

function createWindow() {
    const win = new BrowserWindow({
        width: 1200, height: 800,
        webPreferences: {
            nodeIntegration: true,
            contextIsolation: false,
            enableRemoteModule: true
        }
    });

    // Cấp quyền media
    session.defaultSession.setPermissionRequestHandler((webContents, permission, callback) => {
        callback(true);
    });

    // Cấp quyền chọn màn hình
    session.defaultSession.setDisplayMediaRequestHandler((request, callback) => {
        desktopCapturer.getSources({ types: ['screen'] }).then((sources) => {
            callback({ video: sources[0], audio: 'loopback' });
        }).catch((err) => {
            console.error(err);
            callback(null);
        });
    });

    // Sửa dòng này:
    win.loadFile('login.html'); // Chạy file login trước tiên

    // Trong file main.js, thêm vào sau dòng setPermissionRequestHandler
    session.defaultSession.setPermissionCheckHandler((webContents, permission, requestingOrigin, details) => {
        if (permission === 'media' || permission === 'video_capture') {
            return true;
        }
        return false;
    });
}

app.whenReady().then(createWindow);
app.on('window-all-closed', () => { if (process.platform !== 'darwin') app.quit(); });