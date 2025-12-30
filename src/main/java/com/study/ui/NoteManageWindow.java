package com.study.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 * 笔记管理窗口 - 非模态窗口
 * 用于记录和管理学习笔记
 * 学号：你的学号  姓名：你的姓名
 */
public class NoteManageWindow extends JDialog {
    private JList<String> noteList;
    private DefaultListModel<String> listModel;
    private JTextArea noteContentArea;
    private JTextField titleField;
    private JComboBox<String> categoryCombo;
    private JButton saveButton;
    private JButton deleteButton;
    private JButton newButton;
    private ArrayList<Note> notes;
    private int currentNoteIndex = -1;
    private SimpleDateFormat dateFormat;

    public NoteManageWindow(Frame parent) {
        super(parent, "笔记管理", true);
        notes = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        initComponents();
        setupUI();
        loadSampleData();
    }

    private void initComponents() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        // 标题面板（统一风格）
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // 中间：笔记列表和编辑区
        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        // 底部：操作按钮
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("笔记管理");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("记录学习要点，知识永不丢失");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // 控制面板（搜索）
        JPanel controlPanel = createControlPanel();

        // 主内容区域
        JPanel contentPanel = createContentPanel();

        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel searchLabel = new JLabel("搜索笔记：");
        searchLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));

        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("搜索笔记 - 学号：你的学号");

        JButton searchButton = createStyledButton("搜索", new Color(52, 152, 219));
        searchButton.setToolTipText("搜索 - 姓名：你的姓名");
        searchButton.addActionListener(e -> searchNotes(searchField.getText()));

        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(searchButton);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        // 左侧：笔记列表
        JPanel listPanel = createListPanel();

        // 右侧：笔记编辑区
        JPanel editPanel = createEditPanel();

        // 使用分隔面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPanel, editPanel);
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.3);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "笔记列表",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14),
                new Color(52, 73, 94)
        ));

        listModel = new DefaultListModel<>();
        noteList = new JList<>(listModel);
        noteList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        noteList.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        noteList.setBackground(Color.WHITE);
        noteList.setToolTipText("点击选择笔记 - 学号：你的学号 姓名：你的姓名");
        noteList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedNote();
            }
        });

        JScrollPane scrollPane = new JScrollPane(noteList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEditPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                "笔记内容",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 14),
                new Color(52, 73, 94)
        ));

        // 顶部：标题和分类
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        topPanel.add(new JLabel("标题："));
        titleField = new JTextField();
        titleField.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        titleField.setToolTipText("输入笔记标题 - 学号：你的学号");
        topPanel.add(titleField);

        topPanel.add(new JLabel("分类："));
        String[] categories = {"数学", "编程", "英语", "专业课", "其他"};
        categoryCombo = new JComboBox<>(categories);
        categoryCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        categoryCombo.setToolTipText("选择笔记分类 - 姓名：你的姓名");
        topPanel.add(categoryCombo);

        // 中间：内容编辑区
        noteContentArea = new JTextArea();
        noteContentArea.setLineWrap(true);
        noteContentArea.setWrapStyleWord(true);
        noteContentArea.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        noteContentArea.setToolTipText("编辑笔记内容 - 学号：你的学号 姓名：你的姓名");

        JScrollPane scrollPane = new JScrollPane(noteContentArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        newButton = createStyledButton("新建笔记", new Color(46, 204, 113));
        newButton.setToolTipText("创建新笔记 - 学号：你的学号 姓名：你的姓名");
        newButton.addActionListener(e -> createNewNote());

        saveButton = createStyledButton("保存笔记", new Color(52, 152, 219));
        saveButton.setToolTipText("保存当前笔记 - 学号：你的学号");
        saveButton.addActionListener(e -> saveNote());

        deleteButton = createStyledButton("删除笔记", new Color(231, 76, 60));
        deleteButton.setToolTipText("删除当前笔记 - 姓名：你的姓名");
        deleteButton.addActionListener(e -> deleteNote());

        JButton exportButton = createStyledButton("导出笔记", new Color(155, 89, 182));
        exportButton.setToolTipText("导出笔记为TXT文件");
        exportButton.addActionListener(e -> exportNotes());

        panel.add(newButton);
        panel.add(saveButton);
        panel.add(deleteButton);
        panel.add(exportButton);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);

        // 关键设置：禁止文字省略
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);

        // 计算最佳宽度（确保足够显示完整文字）
        FontMetrics metrics = button.getFontMetrics(button.getFont());
        int textWidth = metrics.stringWidth(text);

        // 使用固定边距而不是计算，避免按钮过小
        int buttonWidth = textWidth + 40; // 左右各20像素内边距
        int buttonHeight = 35;

        Dimension size = new Dimension(buttonWidth, buttonHeight);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }


    private void loadSampleData() {
        notes.add(new Note("Java基础笔记", "编程", "1. 面向对象编程\n2. 继承与多态\n3. 接口与抽象类"));
        notes.add(new Note("数据结构复习", "数学", "• 栈和队列\n• 树和图\n• 排序算法"));
        notes.add(new Note("英语单词", "英语", "innovative - 创新的\nefficient - 高效的"));

        updateNoteList();
    }

    private void updateNoteList() {
        listModel.clear();
        for (Note note : notes) {
            listModel.addElement(note.getTitle() + " [" + note.getCategory() + "]");
        }
    }

    private void loadSelectedNote() {
        int index = noteList.getSelectedIndex();
        if (index >= 0 && index < notes.size()) {
            currentNoteIndex = index;
            Note note = notes.get(index);
            titleField.setText(note.getTitle());
            categoryCombo.setSelectedItem(note.getCategory());
            noteContentArea.setText(note.getContent());
        }
    }

    private void createNewNote() {
        titleField.setText("");
        noteContentArea.setText("");
        categoryCombo.setSelectedIndex(0);
        currentNoteIndex = -1;
        noteList.clearSelection();
        titleField.requestFocus();
    }

    private void saveNote() {
        String title = titleField.getText().trim();
        String content = noteContentArea.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入笔记标题", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentNoteIndex >= 0) {
            // 更新现有笔记
            Note note = notes.get(currentNoteIndex);
            note.setTitle(title);
            note.setCategory(category);
            note.setContent(content);
            note.setLastModified(new Date());
        } else {
            // 创建新笔记
            notes.add(new Note(title, category, content));
            currentNoteIndex = notes.size() - 1;
        }

        updateNoteList();
        noteList.setSelectedIndex(currentNoteIndex);
        JOptionPane.showMessageDialog(this, "笔记已保存！\n学号：你的学号 姓名：你的姓名");
    }

    private void deleteNote() {
        if (currentNoteIndex < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的笔记", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "确定要删除这条笔记吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            notes.remove(currentNoteIndex);
            updateNoteList();
            createNewNote();
            JOptionPane.showMessageDialog(this, "笔记已删除");
        }
    }

    private void searchNotes(String keyword) {
        if (keyword.trim().isEmpty()) {
            updateNoteList();
            return;
        }

        listModel.clear();
        for (int i = 0; i < notes.size(); i++) {
            Note note = notes.get(i);
            if (note.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    note.getContent().toLowerCase().contains(keyword.toLowerCase())) {
                listModel.addElement(note.getTitle() + " [" + note.getCategory() + "]");
            }
        }

        if (listModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到匹配的笔记", "搜索结果", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportNotes() {
        // 创建选择菜单
        JPopupMenu exportMenu = new JPopupMenu();
        JMenuItem exportCurrent = new JMenuItem("导出当前笔记");
        JMenuItem exportAll = new JMenuItem("导出所有笔记");

        exportCurrent.addActionListener(e -> exportCurrentNote());
        exportAll.addActionListener(e -> exportAllNotes());

        exportMenu.add(exportCurrent);
        exportMenu.addSeparator();
        exportMenu.add(exportAll);

        // 找到导出按钮的位置
        Component[] components = getContentPane().getComponents();
        JButton exportButton = null;
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JButton && ((JButton) subComp).getText().equals("导出笔记")) {
                        exportButton = (JButton) subComp;
                        break;
                    }
                }
            }
        }

        if (exportButton != null) {
            exportMenu.show(exportButton, 0, exportButton.getHeight());
        } else {
            exportMenu.show(this, getWidth() - 150, getHeight() - 50);
        }
    }

    private void exportCurrentNote() {
        if (currentNoteIndex < 0) {
            JOptionPane.showMessageDialog(this, "请先选择要导出的笔记", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Note note = notes.get(currentNoteIndex);

        // 创建文件选择器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("导出当前笔记为TXT");
        fileChooser.setSelectedFile(new File(note.getTitle() + ".txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            // 确保文件扩展名是.txt
            String filePath = file.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                file = new File(filePath + ".txt");
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                String content = generateTxtContent(note);
                writer.write(content);
                writer.flush();

                JOptionPane.showMessageDialog(this,
                        "笔记导出成功！\n" +
                                "文件路径: " + file.getAbsolutePath() + "\n\n" +
                                "学号：你的学号 姓名：你的姓名",
                        "导出成功",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "导出失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportAllNotes() {
        if (notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有可导出的笔记", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 创建文件夹选择器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择导出文件夹");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File folder = fileChooser.getSelectedFile();

            if (!folder.exists()) {
                folder.mkdirs();
            }

            int successCount = 0;
            int totalCount = notes.size();

            // 创建进度条对话框
            ProgressMonitor progressMonitor = new ProgressMonitor(this,
                    "正在导出笔记...",
                    "准备导出",
                    0, totalCount);
            progressMonitor.setMillisToDecideToPopup(100);
            progressMonitor.setMillisToPopup(200);

            for (int i = 0; i < notes.size(); i++) {
                if (progressMonitor.isCanceled()) {
                    break;
                }

                Note note = notes.get(i);
                progressMonitor.setNote("正在导出: " + note.getTitle());
                progressMonitor.setProgress(i + 1);

                try {
                    // 清理文件名中的非法字符
                    String safeFileName = note.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_");
                    File file = new File(folder, safeFileName + ".txt");

                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        String content = generateTxtContent(note);
                        writer.write(content);
                        successCount++;
                    }
                } catch (IOException ex) {
                    // 继续导出其他文件
                }
            }

            progressMonitor.close();

            if (!progressMonitor.isCanceled()) {
                // 显示导出结果
                JOptionPane.showMessageDialog(this,
                        "批量导出完成！\n" +
                                "成功导出: " + successCount + " 个文件\n" +
                                "导出目录: " + folder.getAbsolutePath() + "\n\n" +
                                "学号：你的学号 姓名：你的姓名",
                        "批量导出成功",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private String generateTxtContent(Note note) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(50)).append("\n");
        sb.append("笔记标题: ").append(note.getTitle()).append("\n");
        sb.append("笔记分类: ").append(note.getCategory()).append("\n");
        sb.append("创建时间: ").append(dateFormat.format(note.getCreateTime())).append("\n");
        sb.append("最后修改: ").append(dateFormat.format(note.getLastModified())).append("\n");
        sb.append("学号：你的学号 姓名：你的姓名\n");
        sb.append("=".repeat(50)).append("\n\n");
        sb.append("内容:\n");
        sb.append("-".repeat(50)).append("\n");
        sb.append(note.getContent());
        sb.append("\n\n").append("-".repeat(50)).append("\n");
        sb.append("导出时间: ").append(dateFormat.format(new Date()));
        sb.append("\n").append("=".repeat(50));

        return sb.toString();
    }

    // 内部类：笔记
    private class Note {
        private String title;
        private String category;
        private String content;
        private Date createTime;
        private Date lastModified;

        public Note(String title, String category, String content) {
            this.title = title;
            this.category = category;
            this.content = content;
            this.createTime = new Date();
            this.lastModified = new Date();
        }

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public Date getCreateTime() { return createTime; }

        public Date getLastModified() { return lastModified; }
        public void setLastModified(Date lastModified) { this.lastModified = lastModified; }
    }
}