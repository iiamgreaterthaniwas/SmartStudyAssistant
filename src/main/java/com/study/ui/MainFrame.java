package com.study.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 主窗体 - 智能学习助手系统主界面
 * 学号：你的学号  姓名：你的姓名
 */
public class MainFrame extends JFrame {
    private JMenuBar menuBar;
    private JToolBar toolBar;
    private JPanel contentPanel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private Timer clockTimer;

    // 子窗体实例
    private PomodoroWindow pomodoroWindow;
    private NoteManageWindow noteWindow;
    private TaskManageWindow taskWindow;
    private StatisticsWindow statsWindow;
    private SettingsWindow settingsWindow;
    private ReminderWindow reminderWindow;
    // 学习计划管理器面板
    private StudyPlanManager studyPlanManager;
    private String currentTheme = "浅色主题"; // 当前主题
    private int currentFontSize = 14; // 当前字体大小
    public MainFrame() {
        initComponents();
        setupUI();
        startClock();
        applyTheme("浅色主题", 14); // 或调用一个初始化的方法
    }
    public void applyTheme(String theme, int fontSize) {
        this.currentTheme = theme;
        this.currentFontSize = fontSize;

        Color bgColor, fgColor, accentColor, cardBgColor;

        if (theme.equals("深色主题")) {
            bgColor = new Color(45, 45, 48);
            fgColor = new Color(220, 220, 220);
            accentColor = Color.BLACK;
            cardBgColor = new Color(37, 37, 38);
        } else { // 浅色主题
            bgColor = new Color(240, 248, 255);
            fgColor = new Color(20, 20, 20);
            accentColor = new Color(41, 128, 185);
            cardBgColor = Color.WHITE;
        }

        // 应用主题到主面板
        applyThemeToComponent(contentPanel, bgColor, fgColor, cardBgColor, fontSize);


        // 应用到工具栏
        applyThemeToToolBar(toolBar, bgColor, fgColor, accentColor, fontSize);

        // 应用到状态栏
        Container statusBar = ((JPanel)getContentPane().getComponent(2)); // 状态栏是第三个组件
        statusBar.setBackground(bgColor);
        statusLabel.setForeground(fgColor);
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, fontSize - 2));
        timeLabel.setForeground(fgColor);
        timeLabel.setFont(new Font("微软雅黑", Font.PLAIN, fontSize - 2));

        // 刷新界面
        revalidate();
        repaint();

        statusLabel.setText("  主题已切换为：" + theme + "，字体大小：" + fontSize + "px");
    }

    // 递归应用主题到组件
    private void applyThemeToComponent(Component comp, Color bgColor, Color fgColor, Color cardBgColor, int fontSize) {
        if (comp instanceof JPanel) {
            JPanel panel = (JPanel) comp;

            // 保持卡片的背景色
            if (panel.getCursor().getType() == Cursor.HAND_CURSOR) {
                panel.setBackground(cardBgColor);
            } else {
                panel.setBackground(bgColor);
            }

            // 递归处理子组件
            for (Component child : panel.getComponents()) {
                applyThemeToComponent(child, bgColor, fgColor, cardBgColor, fontSize);
            }
        } else if (comp instanceof JLabel) {
            JLabel label = (JLabel) comp;
            label.setForeground(fgColor);

            // 调整字体大小
            Font currentFont = label.getFont();
            if (currentFont.getSize() >= 36) { // 大标题
                label.setFont(new Font(currentFont.getName(), currentFont.getStyle(), fontSize + 20));
            } else if (currentFont.getSize() >= 18) { // 中标题
                label.setFont(new Font(currentFont.getName(), currentFont.getStyle(), fontSize + 4));
            } else { // 普通文本
                label.setFont(new Font(currentFont.getName(), currentFont.getStyle(), fontSize));
            }
        } else if (comp instanceof Container) {
            Container container = (Container) comp;
            for (Component child : container.getComponents()) {
                applyThemeToComponent(child, bgColor, fgColor, cardBgColor, fontSize);
            }
        }
    }

    // 应用主题到菜单栏
    private void applyThemeToMenuBar(JMenuBar menuBar, Color bgColor, Color fgColor, int fontSize) {
        menuBar.setBackground(bgColor);

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
            menu.setBackground(bgColor);
            menu.setForeground(fgColor);
            menu.setFont(new Font("微软雅黑", Font.PLAIN, fontSize));

            for (int j = 0; j < menu.getItemCount(); j++) {
                JMenuItem item = menu.getItem(j);
                if (item != null) {
                    item.setBackground(bgColor);
                    item.setForeground(fgColor);
                    item.setFont(new Font("微软雅黑", Font.PLAIN, fontSize - 1));
                }
            }
        }
    }

    // 应用主题到工具栏
    private void applyThemeToToolBar(JToolBar toolBar, Color bgColor, Color fgColor, Color accentColor, int fontSize) {
        toolBar.setBackground(bgColor);

        for (Component comp : toolBar.getComponents()) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                btn.setBackground(accentColor);
                btn.setForeground(Color.WHITE);
                btn.setFont(new Font("微软雅黑", Font.PLAIN, fontSize - 1));

                // 添加鼠标悬停效果
                btn.addMouseListener(new MouseAdapter() {
                    private Color originalBg = btn.getBackground();

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        btn.setBackground(accentColor.brighter());
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        btn.setBackground(originalBg);
                    }
                });
            }
        }
    }
    // 获取当前主题（供SettingsWindow使用）
    public String getCurrentTheme() {
        return currentTheme;
    }

    // 获取当前字体大小（供SettingsWindow使用）
    public int getCurrentFontSize() {
        return currentFontSize;
    }
    private void initComponents() {
        setTitle("智能学习助手系统 - Smart Study Assistant");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 设置图标（如果有的话）
        try {
            setIconImage(Toolkit.getDefaultToolkit().createImage("resources/icons/app_icon.png"));
        } catch (Exception e) {
            // 图标加载失败时不影响程序运行
        }
    }

    private void setupUI() {
        // 创建菜单栏
        createMenuBar();

        // 创建工具栏
        createToolBar();

        // 创建主内容面板
        createContentPanel();

        // 创建状态栏
        createStatusBar();
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // 加载图标
        ImageIcon planIcon = createImageIcon("resources/icons/plan_icon.png", "学习计划");
        ImageIcon toolsIcon = createImageIcon("resources/icons/tools_icon.png", "学习工具");
        ImageIcon analysisIcon = createImageIcon("resources/icons/analysis_icon.png", "数据分析");
        ImageIcon settingsIcon = createImageIcon("resources/icons/settings_icon.png", "设置");
        ImageIcon helpIcon = createImageIcon("resources/icons/help_icon.png", "帮助");

        // 二级菜单图标
        ImageIcon newPlanIcon = createImageIcon("resources/icons/new_icon.png", "新建");
        ImageIcon viewPlanIcon = createImageIcon("resources/icons/view_icon.png", "查看");
        ImageIcon exitIcon = createImageIcon("resources/icons/exit_icon.png", "退出");
        ImageIcon pomodoroIcon = createImageIcon("resources/icons/pomodoro_icon.png", "番茄钟");
        ImageIcon noteIcon = createImageIcon("resources/icons/note_icon.png", "笔记");
        ImageIcon taskIcon = createImageIcon("resources/icons/task_icon.png", "任务");
        ImageIcon statsIcon = createImageIcon("resources/icons/stats_icon.png", "统计");
        ImageIcon planStatsIcon = createImageIcon("resources/icons/plan_stats_icon.png", "计划统计");
        ImageIcon prefIcon = createImageIcon("resources/icons/pref_icon.png", "偏好设置");
        ImageIcon themeIcon = createImageIcon("resources/icons/theme_icon.png", "主题切换");
        ImageIcon fontSizeIcon = createImageIcon("resources/icons/font_size_icon.png", "字体大小");
        ImageIcon helpContentIcon = createImageIcon("resources/icons/help_content_icon.png", "帮助主题");
        ImageIcon aboutIcon = createImageIcon("resources/icons/about_icon.png", "关于");

        // 三级菜单图标（字体大小选项）
        ImageIcon smallFontIcon = createImageIcon("resources/icons/small_font.png", "小字体");
        ImageIcon defaultFontIcon = createImageIcon("resources/icons/default_font.png", "默认字体");
        ImageIcon mediumFontIcon = createImageIcon("resources/icons/medium_font.png", "中等字体");
        ImageIcon largeFontIcon = createImageIcon("resources/icons/large_font.png", "大字体");
        ImageIcon extraLargeFontIcon = createImageIcon("resources/icons/extra_large_font.png", "特大字体");

        // 文件菜单（学习计划）
        JMenu fileMenu = new JMenu("学习计划(F)");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        if (planIcon != null) {
            fileMenu.setIcon(planIcon);
        }

        JMenuItem newStudyItem = new JMenuItem("新建学习计划(N)", KeyEvent.VK_N);
        newStudyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        newStudyItem.setToolTipText("创建新的学习计划 - 学号：你的学号 姓名：你的姓名");
        if (newPlanIcon != null) {
            newStudyItem.setIcon(newPlanIcon);
        }
        newStudyItem.addActionListener(e -> showDataInputWindow());

        // 添加查看学习计划菜单项
        JMenuItem viewPlansItem = new JMenuItem("查看学习计划(V)", KeyEvent.VK_V);
        viewPlansItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        viewPlansItem.setToolTipText("查看和管理学习计划");
        if (viewPlanIcon != null) {
            viewPlansItem.setIcon(viewPlanIcon);
        }
        viewPlansItem.addActionListener(e -> showStudyPlanManager());

        JMenuItem exitItem = new JMenuItem("退出(X)", KeyEvent.VK_X);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
        if (exitIcon != null) {
            exitItem.setIcon(exitIcon);
        }
        exitItem.addActionListener(e -> exitApplication());

        fileMenu.add(newStudyItem);
        fileMenu.add(viewPlansItem); // 新增
        fileMenu.addSeparator();
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // 学习工具菜单
        JMenu toolsMenu = new JMenu("学习工具(T)");
        toolsMenu.setMnemonic(KeyEvent.VK_T);
        if (toolsIcon != null) {
            toolsMenu.setIcon(toolsIcon);
        }

        JMenuItem pomodoroItem = new JMenuItem("番茄钟(P)", KeyEvent.VK_P);
        pomodoroItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        pomodoroItem.setToolTipText("打开番茄钟计时器 - 学号：你的学号 姓名：你的姓名");
        if (pomodoroIcon != null) {
            pomodoroItem.setIcon(pomodoroIcon);
        }
        pomodoroItem.addActionListener(e -> showPomodoroWindow());

        JMenuItem noteItem = new JMenuItem("笔记管理(N)", KeyEvent.VK_N);
        noteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        noteItem.setToolTipText("管理学习笔记");
        if (noteIcon != null) {
            noteItem.setIcon(noteIcon);
        }
        noteItem.addActionListener(e -> showNoteWindow());

        JMenuItem taskItem = new JMenuItem("任务管理(T)", KeyEvent.VK_T);
        taskItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        taskItem.setToolTipText("管理学习任务");
        if (taskIcon != null) {
            taskItem.setIcon(taskIcon);
        }
        taskItem.addActionListener(e -> showTaskWindow());

        toolsMenu.add(pomodoroItem);
        toolsMenu.add(noteItem);
        toolsMenu.add(taskItem);

        // 数据分析菜单
        JMenu analysisMenu = new JMenu("数据分析(A)");
        analysisMenu.setMnemonic(KeyEvent.VK_A);
        if (analysisIcon != null) {
            analysisMenu.setIcon(analysisIcon);
        }

        JMenuItem statsItem = new JMenuItem("学习统计(S)", KeyEvent.VK_S);
        statsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        statsItem.setToolTipText("查看学习统计数据");
        if (statsIcon != null) {
            statsItem.setIcon(statsIcon);
        }
        statsItem.addActionListener(e -> showStatisticsWindow());

        JMenuItem planStatsItem = new JMenuItem("计划完成统计");
        planStatsItem.setToolTipText("统计学习计划完成情况");
        if (planStatsIcon != null) {
            planStatsItem.setIcon(planStatsIcon);
        }
        planStatsItem.addActionListener(e -> showPlanStatistics());

        analysisMenu.add(statsItem);
        analysisMenu.add(planStatsItem);

        // 设置菜单
        JMenu settingsMenu = new JMenu("设置(S)");
        settingsMenu.setMnemonic(KeyEvent.VK_S);
        if (settingsIcon != null) {
            settingsMenu.setIcon(settingsIcon);
        }

        JMenuItem preferencesItem = new JMenuItem("偏好设置(P)", KeyEvent.VK_P);
        preferencesItem.setToolTipText("设置系统偏好 - 学号：你的学号");
        if (prefIcon != null) {
            preferencesItem.setIcon(prefIcon);
        }
        preferencesItem.addActionListener(e -> showSettingsWindow());

        JMenuItem themeItem = new JMenuItem("主题切换(T)");
        themeItem.setToolTipText("切换界面主题");
        if (themeIcon != null) {
            themeItem.setIcon(themeIcon);
        }
        themeItem.addActionListener(e -> switchTheme());

        // 添加字体大小调节的级联菜单（二级菜单）
        JMenu fontSizeMenu = new JMenu("字体大小(F)");
        fontSizeMenu.setToolTipText("调整界面字体大小");
        fontSizeMenu.setMnemonic(KeyEvent.VK_F);
        if (fontSizeIcon != null) {
            fontSizeMenu.setIcon(fontSizeIcon);
        }

        // 创建字体大小选项（三级菜单）
        String[] fontSizeOptions = {"小 (12px)", "默认 (14px)", "中 (16px)", "大 (18px)", "特大 (20px)"};
        int[] fontSizes = {12, 14, 16, 18, 20};
        ImageIcon[] fontIcons = {smallFontIcon, defaultFontIcon, mediumFontIcon, largeFontIcon, extraLargeFontIcon};

        // 创建按钮组以确保单选
        ButtonGroup fontSizeGroup = new ButtonGroup();

        for (int i = 0; i < fontSizeOptions.length; i++) {
            JRadioButtonMenuItem fontSizeItem = new JRadioButtonMenuItem(fontSizeOptions[i]);
            final int fontSize = fontSizes[i];

            // 设置图标
            if (fontIcons[i] != null) {
                fontSizeItem.setIcon(fontIcons[i]);
            }

            // 如果当前字体大小匹配，则选中
            if (fontSize == currentFontSize) {
                fontSizeItem.setSelected(true);
            }

            fontSizeItem.addActionListener(e -> {
                // 应用新的字体大小（保持当前主题）
                applyTheme(currentTheme, fontSize);

                // 更新状态栏提示
                statusLabel.setText("  字体大小已设置为: " + fontSize + "px");
            });

            // 添加到按钮组
            fontSizeGroup.add(fontSizeItem);
            // 添加到字体菜单
            fontSizeMenu.add(fontSizeItem);
        }

        settingsMenu.add(preferencesItem);
        settingsMenu.add(themeItem);
        settingsMenu.add(fontSizeMenu);

        // 帮助菜单
        JMenu helpMenu = new JMenu("帮助(H)");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        if (helpIcon != null) {
            helpMenu.setIcon(helpIcon);
        }

        JMenuItem helpContentItem = new JMenuItem("帮助文档(H)", KeyEvent.VK_H);
        helpContentItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        helpContentItem.setToolTipText("查看帮助文档 - 学号：你的学号 姓名：你的姓名");
        if (helpContentIcon != null) {
            helpContentItem.setIcon(helpContentIcon);
        }
        helpContentItem.addActionListener(e -> showHelp());

        JMenuItem aboutItem = new JMenuItem("关于(A)", KeyEvent.VK_A);
        aboutItem.setToolTipText("关于本软件");
        if (aboutIcon != null) {
            aboutItem.setIcon(aboutIcon);
        }
        aboutItem.addActionListener(e -> showAbout());

        helpMenu.add(helpContentItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        // 添加所有菜单到菜单栏
        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(analysisMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }
    /**
     * 创建ImageIcon对象，用于加载菜单图标
     * @param path 图标路径
     * @param description 图标描述
     * @return ImageIcon对象，如果加载失败则返回null
     */
    private ImageIcon createImageIcon(String path, String description) {
        try {
            Image img = Toolkit.getDefaultToolkit().createImage(path);
            if (img != null) {
                // 可以调整图标大小，例如 16x16
                img = img.getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                return new ImageIcon(img, description);
            }
        } catch (Exception e) {
            // 图标加载失败时返回null，不影响程序运行
            System.err.println("无法加载图标: " + path);
        }
        return null;
    }
    private void createToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // 新建计划按钮
        JButton newBtn = createToolBarButton("新建计划", "创建新学习计划", e -> showDataInputWindow());

        // 查看计划按钮
        JButton viewPlansBtn = createToolBarButton("查看计划", "查看和管理学习计划", e -> showStudyPlanManager());

        // 番茄钟按钮
        JButton pomodoroBtn = createToolBarButton("番茄钟", "打开番茄钟 - 学号：你的学号", e -> showPomodoroWindow());

        // 笔记按钮
        JButton noteBtn = createToolBarButton("笔记", "笔记管理", e -> showNoteWindow());

        // 任务按钮
        JButton taskBtn = createToolBarButton("任务", "任务管理", e -> showTaskWindow());

        // 统计按钮
        JButton statsBtn = createToolBarButton("统计", "学习统计", e -> showStatisticsWindow());

        // 设置按钮
        JButton settingsBtn = createToolBarButton("设置", "系统设置", e -> showSettingsWindow());

        // 帮助按钮
        JButton helpBtn = createToolBarButton("帮助", "帮助文档 - 姓名：你的姓名", e -> showHelp());

        toolBar.add(newBtn);
        toolBar.add(viewPlansBtn); // 新增
        toolBar.addSeparator();
        toolBar.add(pomodoroBtn);
        toolBar.add(noteBtn);
        toolBar.add(taskBtn);
        toolBar.addSeparator();
        toolBar.add(statsBtn);
        toolBar.addSeparator();
        toolBar.add(settingsBtn);
        toolBar.add(helpBtn);

        add(toolBar, BorderLayout.NORTH);
    }

    private JButton createToolBarButton(String text, String tooltip, ActionListener listener) {
        JButton button = new JButton(text);
        button.setToolTipText(tooltip);
        button.setFocusable(false);
        button.addActionListener(listener);
        return button;
    }

    private void createContentPanel() {
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建欢迎面板
        JPanel welcomePanel = createWelcomePanel();
        contentPanel.add(welcomePanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // 欢迎标题
        JLabel titleLabel = new JLabel("智能学习助手系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(new Color(41, 128, 185));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        panel.add(titleLabel, gbc);

        // 副标题
        JLabel subtitleLabel = new JLabel("Smart Study Assistant - 让学习更高效");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        gbc.gridy = 1;
        panel.add(subtitleLabel, gbc);

        // 功能卡片面板
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(40, 20, 20, 20);

        // 学习计划卡片
        JPanel planCard = createFeatureCard("学习计划", "制定和管理学习计划", e -> showStudyPlanManager());
        gbc.gridx = 0;
        panel.add(planCard, gbc);

        // 番茄钟卡片
        JPanel pomodoroCard = createFeatureCard("番茄钟", "专注学习，高效管理时间", e -> showPomodoroWindow());
        gbc.gridx = 1;
        panel.add(pomodoroCard, gbc);

        // 笔记管理卡片
        JPanel noteCard = createFeatureCard("笔记管理", "记录学习要点，知识永不丢失", e -> showNoteWindow());
        gbc.gridx = 2;
        panel.add(noteCard, gbc);

        // 第二行功能卡片
        gbc.gridy = 3;

        // 任务管理卡片
        JPanel taskCard = createFeatureCard("任务管理", "规划学习任务，追踪完成进度", e -> showTaskWindow());
        gbc.gridx = 0;
        panel.add(taskCard, gbc);

        // 数据统计卡片
        JPanel statsCard = createFeatureCard("数据统计", "可视化学习数据，了解学习状态", e -> showStatisticsWindow());
        gbc.gridx = 1;
        panel.add(statsCard, gbc);

        // 智能提醒卡片
        // 智能提醒卡片
        JPanel reminderCard = createFeatureCard("智能提醒", "基于遗忘曲线的复习提醒", e -> showReminderWindow());
        gbc.gridx = 2;
        panel.add(reminderCard, gbc);

        return panel;
    }
    private void showReminderWindow() {
        if (reminderWindow == null || !reminderWindow.isDisplayable()) {
            reminderWindow = new ReminderWindow(this);
        }
        reminderWindow.setVisible(true);
        reminderWindow.toFront();
        statusLabel.setText("  智能提醒窗口已打开");
    }
    private JPanel createFeatureCard(String title, String description, ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(250, 150));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 标题
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 描述
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        descLabel.setForeground(new Color(127, 140, 141));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(descLabel, BorderLayout.CENTER);

        // 添加点击事件
        card.addMouseListener(new MouseAdapter() {
            // 不存储固定的 originalBg，而是动态获取

            @Override
            public void mouseEntered(MouseEvent e) {
                // 根据当前主题设置悬停颜色
                if (currentTheme.equals("深色主题")) {
                    card.setBackground(new Color(60, 63, 65)); // 深色主题的悬停色
                } else {
                    card.setBackground(new Color(236, 240, 241)); // 浅色主题的悬停色
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // 动态获取当前主题下的卡片背景色
                if (currentTheme.equals("深色主题")) {
                    card.setBackground(new Color(37, 37, 38)); // 深色主题的背景色
                } else {
                    card.setBackground(Color.WHITE); // 浅色主题的背景色
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // 触发点击事件
                listener.actionPerformed(new ActionEvent(card, ActionEvent.ACTION_PERFORMED, ""));
            }
        });

        return card;
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(getWidth(), 25));

        statusLabel = new JLabel("  就绪");
        timeLabel = new JLabel("  ");
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            timeLabel.setText(sdf.format(new Date()) + "  ");
        });
        clockTimer.start();
    }

    // 显示学习计划管理器
    private void showStudyPlanManager() {
        if (studyPlanManager == null || !studyPlanManager.isDisplayable()) {
            studyPlanManager = new StudyPlanManager(this); // 需要修改StudyPlanManager的构造函数
        }
        studyPlanManager.setVisible(true);
        studyPlanManager.toFront();
        statusLabel.setText("  学习计划管理器已打开");
    }

    // 显示各个子窗体的方法
    private void showPomodoroWindow() {
        if (pomodoroWindow == null || !pomodoroWindow.isDisplayable()) {
            pomodoroWindow = new PomodoroWindow(this);
        }
        pomodoroWindow.setVisible(true);
        statusLabel.setText("  番茄钟已打开");
    }

    private void showNoteWindow() {
        if (noteWindow == null || !noteWindow.isDisplayable()) {
            noteWindow = new NoteManageWindow(this);
        }
        noteWindow.setVisible(true);
        noteWindow.toFront();
        statusLabel.setText("  笔记管理已打开");
    }

    private void showTaskWindow() {
        if (taskWindow == null || !taskWindow.isDisplayable()) {
            taskWindow = new TaskManageWindow(this);
        }
        taskWindow.setVisible(true);
        taskWindow.toFront();
        statusLabel.setText("  任务管理已打开");
    }

    private void showStatisticsWindow() {
        if (statsWindow == null || !statsWindow.isDisplayable()) {
            statsWindow = new StatisticsWindow(this);
        }
        statsWindow.setVisible(true);
        statsWindow.toFront();
        statusLabel.setText("  统计分析已打开");
    }

    private void showSettingsWindow() {
        if (settingsWindow == null || !settingsWindow.isDisplayable()) {
            settingsWindow = new SettingsWindow(this);
        }
        settingsWindow.setVisible(true);
        statusLabel.setText("  设置窗口已打开");
    }

    private void showDataInputWindow() {
        DataInputWindow dataInputWindow = new DataInputWindow(this);
        dataInputWindow.setVisible(true);
        statusLabel.setText("  新建学习计划");
    }

    // 显示计划统计
    private void showPlanStatistics() {
        JOptionPane.showMessageDialog(this,
                "学习计划完成统计：\n\n" +
                        "当前共有 3 个学习计划\n" +
                        "已完成：1 个\n" +
                        "进行中：2 个\n" +
                        "未开始：0 个\n\n" +
                        "本周学习时长：12小时 30分钟\n" +
                        "本月学习时长：48小时 15分钟\n\n" +
                        "学号：你的学号 姓名：你的姓名",
                "计划统计",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // 其他功能方法
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            statusLabel.setText("  文件打开功能已触发");
            JOptionPane.showMessageDialog(this, "文件打开功能已加载\n学号：你的学号 姓名：你的姓名");
        }
    }

    private void saveData() {
        statusLabel.setText("  正在保存数据...");
        JOptionPane.showMessageDialog(this, "数据保存功能已加载\n学号：你的学号 姓名：你的姓名");
        statusLabel.setText("  数据已保存");
    }

    private void exportReport() {
        statusLabel.setText("  导出报告功能已触发");
        JOptionPane.showMessageDialog(this, "报告导出功能已加载\n学号：你的学号 姓名：你的姓名");
    }

    private void showTrendAnalysis() {
        statusLabel.setText("  趋势分析功能已触发");
        JOptionPane.showMessageDialog(this, "趋势分析功能已加载\n学号：你的学号 姓名：你的姓名");
    }

    private void switchTheme() {
        // 简单的主题切换（在浅色和深色之间切换）
        String newTheme = currentTheme.equals("浅色主题") ? "深色主题" : "浅色主题";
        applyTheme(newTheme, currentFontSize);
    }

    private void showReminderInfo() {
        JOptionPane.showMessageDialog(this,
                "智能提醒功能说明：\n\n" +
                        "基于艾宾浩斯遗忘曲线，系统会在以下时间点提醒您复习：\n" +
                        "• 学习后 20分钟\n" +
                        "• 学习后 1小时\n" +
                        "• 学习后 8小时\n" +
                        "• 学习后 1天\n" +
                        "• 学习后 2天\n" +
                        "• 学习后 6天\n" +
                        "• 学习后 31天\n\n" +
                        "学号：你的学号 姓名：你的姓名",
                "智能提醒",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelp() {
        try {
            // 尝试打开CHM帮助文件
            Runtime.getRuntime().exec("hh.exe resources/help/help.chm");
            statusLabel.setText("  帮助文档已打开");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "帮助系统说明：\n\n" +
                            "本系统提供完整的CHM帮助文档，包含：\n" +
                            "• 系统介绍\n" +
                            "• 功能使用说明\n" +
                            "• 快捷键参考\n" +
                            "• 常见问题解答\n" +
                            "• 更新日志\n\n" +
                            "学号：你的学号 姓名：你的姓名",
                    "帮助",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "智能学习助手系统 v1.0\n" +
                        "Smart Study Assistant\n\n" +
                        "一个集成番茄钟、笔记管理、任务管理、\n" +
                        "数据统计于一体的智能学习辅助工具\n\n" +
                        "开发者：你的学号 你的姓名\n" +
                        "课程：人机界面设计\n" +
                        "学期：2025-2026学年第1学期\n" +
                        "版权所有 © 2025",
                "关于",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitApplication() {
        int choice = JOptionPane.showConfirmDialog(this,
                "确定要退出智能学习助手系统吗？",
                "退出确认",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (clockTimer != null) {
                clockTimer.stop();
            }
            System.exit(0);
        }
    }
}