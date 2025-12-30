package com.study.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计分析窗口 - 非模态窗口
 * 用于展示学习数据统计和分析
 * 学号：你的学号  姓名：你的姓名
 */
public class StatisticsWindow extends JDialog {

    private JComboBox<String> periodCombo;
    private Map<String, ChartData> chartDataMap;
    private JPanel chart1Content, chart2Content, chart3Content, chart4Content;
    private JPanel card1, card2, card3, card4;

    // 当前选择的统计周期
    private String currentPeriod = "本月";

    public StatisticsWindow(Frame parent) {
        super(parent, "学习统计分析", true);
        initData(); // 初始化数据
        initComponents();
        setupUI();
    }

    private void initComponents() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initData() {
        chartDataMap = new HashMap<>();

        // 为每个统计周期创建不同的数据
        // 今天
        ChartData todayData = new ChartData(
                new double[]{2.5, 3.0, 4.0, 2.0, 3.5, 1.5, 2.0}, // 学习时长
                new int[]{85, 10, 5, 0}, // 任务完成情况
                new double[]{40, 30, 20, 10}, // 学科分布
                new double[]{80, 85, 90, 75, 88, 92, 87} // 专注度
        );
        chartDataMap.put("今天", todayData);

        // 本周
        ChartData weekData = new ChartData(
                new double[]{3.5, 4.2, 3.8, 4.5, 3.0, 2.8, 4.0},
                new int[]{70, 20, 8, 2},
                new double[]{35, 25, 20, 20},
                new double[]{75, 80, 85, 78, 88, 90, 85}
        );
        chartDataMap.put("本周", weekData);

        // 本月
        ChartData monthData = new ChartData(
                new double[]{4.2, 3.8, 4.5, 4.0, 3.5, 4.8, 5.0},
                new int[]{60, 25, 10, 5},
                new double[]{30, 25, 25, 20},
                new double[]{80, 82, 85, 83, 87, 90, 88}
        );
        chartDataMap.put("本月", monthData);

        // 本学期
        ChartData termData = new ChartData(
                new double[]{5.0, 4.8, 5.2, 4.5, 5.5, 4.0, 5.8},
                new int[]{55, 30, 10, 5},
                new double[]{25, 30, 25, 20},
                new double[]{82, 84, 86, 85, 88, 92, 90}
        );
        chartDataMap.put("本学期", termData);

        // 全部
        ChartData allData = new ChartData(
                new double[]{6.0, 5.5, 6.2, 5.0, 6.5, 5.8, 7.0},
                new int[]{50, 35, 10, 5},
                new double[]{20, 30, 30, 20},
                new double[]{84, 86, 88, 87, 90, 93, 92}
        );
        chartDataMap.put("全部", allData);
    }

    private void setupUI() {
        setLayout(new BorderLayout(0, 10));

        // 标题面板（统一风格）
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // 中间：统计图表和数据
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

        JLabel titleLabel = new JLabel("学习数据统计分析");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("可视化您的学习数据，了解学习状态");
        subtitleLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(220, 220, 220));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // 顶部：时间范围选择
        JPanel topPanel = createTopControlPanel();

        // 数据卡片
        JPanel cardsPanel = createDataCardsPanel();

        // 图表区域 - 使用滚动面板
        JScrollPane chartsScrollPane = createChartsScrollPane();

        // 创建一个垂直面板来容纳顶部面板和数据卡片
        JPanel northPanel = new JPanel(new BorderLayout(0, 10));
        northPanel.add(topPanel, BorderLayout.NORTH);
        northPanel.add(cardsPanel, BorderLayout.SOUTH);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(chartsScrollPane, BorderLayout.CENTER); // 图表区域占据中心，可以滚动

        return mainPanel;
    }

    private JScrollPane createChartsScrollPane() {
        JPanel chartsPanel = createChartsPanel();

        // 创建滚动面板，确保所有图表都能完整显示
        JScrollPane scrollPane = new JScrollPane(chartsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 设置滚动面板的首选大小
        scrollPane.setPreferredSize(new Dimension(950, 400));

        return scrollPane;
    }

    private JPanel createTopControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel periodLabel = new JLabel("统计周期：");
        periodLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));

        String[] periods = {"今天", "本周", "本月", "本学期", "全部"};
        periodCombo = new JComboBox<>(periods);
        periodCombo.setSelectedIndex(2); // 默认选择"本月"
        periodCombo.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        periodCombo.setToolTipText("选择统计时间段 - 学号：你的学号 姓名：你的姓名");
        periodCombo.addActionListener(e -> {
            String period = (String) periodCombo.getSelectedItem();
            currentPeriod = period;
            updateStatistics(period);
        });

        panel.add(periodLabel);
        panel.add(periodCombo);

        return panel;
    }

    private JPanel createDataCardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 学习时长卡片
        card1 = createDataCard("总学习时长", "42.5 小时", "+15%", new Color(52, 152, 219));

        // 完成任务数卡片
        card2 = createDataCard("完成任务", "28 个", "+12%", new Color(46, 204, 113));

        // 笔记数量卡片
        card3 = createDataCard("笔记数量", "156 篇", "+8%", new Color(155, 89, 182));

        // 专注度卡片
        card4 = createDataCard("平均专注度", "87%", "+5%", new Color(230, 126, 34));

        panel.add(card1);
        panel.add(card2);
        panel.add(card3);
        panel.add(card4);

        return panel;
    }

    private JPanel createDataCard(String title, String value, String change, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 14));
        titleLabel.setForeground(new Color(52, 73, 94));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("微软雅黑", Font.BOLD, 28));
        valueLabel.setForeground(color);

        JLabel changeLabel = new JLabel("较上周 " + change);
        changeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        changeLabel.setForeground(new Color(46, 204, 113));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(valueLabel, BorderLayout.WEST);
        bottomPanel.add(changeLabel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(bottomPanel, BorderLayout.CENTER);

        card.setToolTipText(title + " - 学号：你的学号 姓名：你的姓名");

        return card;
    }

    private JPanel createChartsPanel() {
        // 使用垂直布局，让图表纵向排列
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // 第一行：两个图表横向排列
        JPanel row1 = new JPanel(new GridLayout(1, 2, 15, 15));
        row1.setBackground(Color.WHITE);
        row1.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // 学习时长趋势图
        JPanel chart1 = createChartPanel("学习时长趋势", "模拟柱状图");
        chart1Content = (JPanel) chart1.getComponent(1);

        // 任务完成情况
        JPanel chart2 = createChartPanel("任务完成分析", "模拟饼图");
        chart2Content = (JPanel) chart2.getComponent(1);

        // 设置固定高度以确保图表完整显示
        Dimension chartSize = new Dimension(450, 300);
        chart1.setPreferredSize(chartSize);
        chart1.setMinimumSize(chartSize);
        chart1.setMaximumSize(chartSize);

        chart2.setPreferredSize(chartSize);
        chart2.setMinimumSize(chartSize);
        chart2.setMaximumSize(chartSize);

        row1.add(chart1);
        row1.add(chart2);

        // 第二行：两个图表横向排列
        JPanel row2 = new JPanel(new GridLayout(1, 2, 15, 15));
        row2.setBackground(Color.WHITE);

        // 学科分布
        JPanel chart3 = createChartPanel("学科时间分布", "模拟柱状图");
        chart3Content = (JPanel) chart3.getComponent(1);

        // 专注度变化
        JPanel chart4 = createChartPanel("专注度变化", "模拟折线图");
        chart4Content = (JPanel) chart4.getComponent(1);

        chart3.setPreferredSize(chartSize);
        chart3.setMinimumSize(chartSize);
        chart3.setMaximumSize(chartSize);

        chart4.setPreferredSize(chartSize);
        chart4.setMinimumSize(chartSize);
        chart4.setMaximumSize(chartSize);

        row2.add(chart3);
        row2.add(chart4);

        panel.add(row1);
        panel.add(row2);

        return panel;
    }

    private JPanel createChartPanel(String title, String chartType) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // 设置固定高度
        panel.setPreferredSize(new Dimension(450, 300));
        panel.setMinimumSize(new Dimension(450, 250));

        // 标题
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        titleLabel.setForeground(new Color(52, 73, 94));

        // 模拟图表内容
        JPanel chartContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                ChartData data = chartDataMap.get(currentPeriod);
                if (data == null) return;

                if (chartType.contains("柱状图")) {
                    if (title.contains("学习时长趋势")) {
                        drawBarChart(g2d, width, height, data.getStudyHours());
                    } else if (title.contains("学科时间分布")) {
                        drawSubjectChart(g2d, width, height, data.getSubjectDistribution());
                    }
                } else if (chartType.contains("饼图")) {
                    drawPieChart(g2d, width, height, data.getTaskCompletion());
                } else if (chartType.contains("折线图")) {
                    drawLineChart(g2d, width, height, data.getFocusLevels());
                }
            }
        };
        chartContent.setBackground(Color.WHITE);
        chartContent.setToolTipText(title + " - 学号：你的学号 姓名：你的姓名");

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(chartContent, BorderLayout.CENTER);

        return panel;
    }

    private void drawBarChart(Graphics2D g2d, int width, int height, double[] data) {
        int barCount = data.length;
        int barWidth = (width - 60) / barCount;
        int maxHeight = height - 40;

        // 找到最大值用于缩放
        double maxValue = 0;
        for (double value : data) {
            if (value > maxValue) maxValue = value;
        }

        Color[] colors = {
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(155, 89, 182),
                new Color(241, 196, 15),
                new Color(230, 126, 34),
                new Color(231, 76, 60),
                new Color(149, 165, 166)
        };

        // 绘制坐标轴
        g2d.setColor(new Color(189, 195, 199));
        g2d.drawLine(30, height - 20, width - 10, height - 20); // X轴
        g2d.drawLine(30, 10, 30, height - 20); // Y轴

        // 绘制柱状图
        for (int i = 0; i < barCount; i++) {
            int barHeight = (int) (maxHeight * (data[i] / maxValue));
            int x = 40 + i * barWidth;
            int y = height - 20 - barHeight;

            g2d.setColor(colors[i % colors.length]);
            g2d.fillRect(x, y, barWidth - 10, barHeight);

            // 绘制数值
            g2d.setColor(new Color(52, 73, 94));
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String valueText = String.format("%.1f", data[i]);
            g2d.drawString(valueText, x + (barWidth - 10) / 2 - 10, y - 5);

            // 绘制标签
            g2d.setColor(new Color(127, 140, 141));
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = "第" + (i + 1) + "天";
            g2d.drawString(label, x, height - 5);
        }
    }

    private void drawSubjectChart(Graphics2D g2d, int width, int height, double[] data) {
        int barCount = data.length;
        int barWidth = (width - 60) / barCount;
        int maxHeight = height - 40;

        // 计算总和用于百分比
        double total = 0;
        for (double value : data) total += value;

        Color[] colors = {
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(155, 89, 182),
                new Color(241, 196, 15)
        };

        String[] subjects = {"数学", "语文", "英语", "其他"};

        // 绘制坐标轴
        g2d.setColor(new Color(189, 195, 199));
        g2d.drawLine(30, height - 20, width - 10, height - 20); // X轴
        g2d.drawLine(30, 10, 30, height - 20); // Y轴

        // 绘制柱状图
        for (int i = 0; i < barCount; i++) {
            int barHeight = (int) (maxHeight * (data[i] / 100));
            int x = 40 + i * barWidth;
            int y = height - 20 - barHeight;

            g2d.setColor(colors[i % colors.length]);
            g2d.fillRect(x, y, barWidth - 10, barHeight);

            // 绘制百分比
            g2d.setColor(new Color(52, 73, 94));
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String percentText = String.format("%.0f%%", (data[i] / total) * 100);
            g2d.drawString(percentText, x + (barWidth - 10) / 2 - 10, y - 5);

            // 绘制学科标签
            g2d.setColor(new Color(127, 140, 141));
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            g2d.drawString(subjects[i], x + 5, height - 5);
        }
    }

    private void drawPieChart(Graphics2D g2d, int width, int height, int[] data) {
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;

        Color[] colors = {
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(155, 89, 182),
                new Color(241, 196, 15)
        };

        String[] labels = {"已完成", "进行中", "未开始", "逾期"};

        // 计算总任务数
        int totalTasks = 0;
        for (int value : data) totalTasks += value;

        int[] angles = new int[4];
        for (int i = 0; i < 4; i++) {
            angles[i] = (int) (360 * data[i] / totalTasks);
        }

        int startAngle = 0;
        for (int i = 0; i < angles.length; i++) {
            g2d.setColor(colors[i]);
            g2d.fillArc(centerX - radius, centerY - radius,
                    radius * 2, radius * 2, startAngle, angles[i]);
            startAngle += angles[i];
        }

        // 绘制图例
        int legendY = 20;
        for (int i = 0; i < labels.length; i++) {
            g2d.setColor(colors[i]);
            g2d.fillRect(10, legendY, 15, 15);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("微软雅黑", Font.PLAIN, 11));
            String percentage = String.format("%.1f%%", (data[i] * 100.0 / totalTasks));
            String labelText = labels[i] + " " + data[i] + "个 (" + percentage + ")";
            g2d.drawString(labelText, 30, legendY + 12);
            legendY += 25;
        }
    }

    private void drawLineChart(Graphics2D g2d, int width, int height, double[] data) {
        int pointCount = data.length;
        int maxHeight = height - 40;

        // 找到最大值和最小值用于缩放
        double maxValue = 0, minValue = 100;
        for (double value : data) {
            if (value > maxValue) maxValue = value;
            if (value < minValue) minValue = value;
        }

        // 绘制坐标轴
        g2d.setColor(new Color(189, 195, 199));
        g2d.drawLine(30, height - 20, width - 10, height - 20); // X轴
        g2d.drawLine(30, 10, 30, height - 20); // Y轴

        // 绘制Y轴刻度
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        for (int i = 0; i <= 5; i++) {
            int y = height - 20 - (maxHeight * i / 5);
            int value = (int) (minValue + (maxValue - minValue) * i / 5);
            g2d.drawString(value + "%", 5, y + 4);
            g2d.drawLine(25, y, 30, y);
        }

        // 生成数据点
        int[] xPoints = new int[pointCount];
        int[] yPoints = new int[pointCount];

        for (int i = 0; i < pointCount; i++) {
            xPoints[i] = 40 + (width - 50) * i / (pointCount - 1);
            yPoints[i] = height - 20 - (int) (maxHeight * ((data[i] - minValue) / (maxValue - minValue)));
        }

        // 绘制折线
        g2d.setColor(new Color(52, 152, 219));
        g2d.setStroke(new BasicStroke(2));
        for (int i = 0; i < pointCount - 1; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }

        // 绘制数据点
        g2d.setColor(new Color(46, 204, 113));
        for (int i = 0; i < pointCount; i++) {
            g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);

            // 绘制数值
            g2d.setColor(new Color(52, 73, 94));
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String valueText = String.format("%.0f%%", data[i]);
            g2d.drawString(valueText, xPoints[i] - 10, yPoints[i] - 10);
            g2d.setColor(new Color(46, 204, 113));

            // 绘制X轴标签
            g2d.setColor(new Color(127, 140, 141));
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = "第" + (i + 1) + "次";
            g2d.drawString(label, xPoints[i] - 5, height - 5);
        }
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(52, 73, 94));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));


        JButton exportImageButton = createStyledButton("导出图表图片", new Color(155, 89, 182));
        exportImageButton.setToolTipText("导出当前图表为图片 - 学号：你的学号 姓名：你的姓名");
        exportImageButton.addActionListener(e -> exportChartImages());

        panel.add(exportImageButton);

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
        button.setMaximumSize(size); // 添加这一行

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    private void updateStatistics(String period) {
        currentPeriod = period;

        // 更新卡片数据
        updateCardData(period);

        // 重绘图表
        if (chart1Content != null) chart1Content.repaint();
        if (chart2Content != null) chart2Content.repaint();
        if (chart3Content != null) chart3Content.repaint();
        if (chart4Content != null) chart4Content.repaint();
    }

    private void updateCardData(String period) {
        // 根据不同的统计周期更新卡片数据
        String[][] cardData = {
                // 学习时长, 完成任务, 笔记数量, 专注度
                {"2.5 小时", "3 个", "5 篇", "85%"}, // 今天
                {"26.8 小时", "19 个", "45 篇", "83%"}, // 本周
                {"125.5 小时", "86 个", "156 篇", "87%"}, // 本月
                {"356.2 小时", "192 个", "324 篇", "86%"}, // 本学期
                {"1200.8 小时", "654 个", "890 篇", "88%"} // 全部
        };

        String[] periods = {"今天", "本周", "本月", "本学期", "全部"};
        int index = -1;
        for (int i = 0; i < periods.length; i++) {
            if (periods[i].equals(period)) {
                index = i;
                break;
            }
        }

        if (index >= 0) {
            updateCard(card1, "总学习时长", cardData[index][0]);
            updateCard(card2, "完成任务", cardData[index][1]);
            updateCard(card3, "笔记数量", cardData[index][2]);
            updateCard(card4, "平均专注度", cardData[index][3]);
        }
    }

    private void updateCard(JPanel card, String title, String value) {
        Component[] components = card.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                Component[] subComps = ((JPanel) comp).getComponents();
                for (Component subComp : subComps) {
                    if (subComp instanceof JLabel) {
                        JLabel label = (JLabel) subComp;
                        if (label.getText().contains(title)) {
                            // 这是标题，不做修改
                        } else if (label.getFont().getSize() == 28) {
                            // 这是数值标签
                            Color originalColor = label.getForeground();
                            label.setText(value);
                            label.setForeground(originalColor);
                        }
                    }
                }
            }
        }
    }

    private void exportReport() {
        String report = "=== 学习统计报告 ===\n" +
                "统计周期: " + currentPeriod + "\n" +
                "生成时间: " + new java.util.Date() + "\n\n" +
                "主要指标:\n" +
                "• 总学习时长: 42.5 小时\n" +
                "• 完成任务: 28 个\n" +
                "• 笔记数量: 156 篇\n" +
                "• 平均专注度: 87%\n\n" +
                "分析说明:\n" +
                "根据" + currentPeriod + "的数据分析，您的学习状态良好。\n" +
                "建议继续保持当前的学习节奏。\n\n" +
                "学号：你的学号 姓名：你的姓名";

        JOptionPane.showMessageDialog(this,
                report,
                "导出报告 - " + currentPeriod,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportChartImages() {
        // 创建文件选择器
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("保存图表图片");
        fileChooser.setSelectedFile(new File("统计图表_" + currentPeriod + ".png"));

        // 设置文件过滤器
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png");
            }

            @Override
            public String getDescription() {
                return "PNG图片文件 (*.png)";
            }
        });

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // 确保文件以.png结尾
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            try {
                // 创建一个包含所有图表的大图片
                BufferedImage combinedImage = createCombinedChartImage();

                // 保存图片
                ImageIO.write(combinedImage, "PNG", fileToSave);

                JOptionPane.showMessageDialog(this,
                        "图表图片已成功导出！\n" +
                                "文件路径: " + fileToSave.getAbsolutePath() + "\n" +
                                "统计周期: " + currentPeriod + "\n" +
                                "学号：你的学号 姓名：你的姓名",
                        "导出成功",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "导出失败: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private BufferedImage createCombinedChartImage() {
        // 创建一个足够大的图片来容纳所有图表
        int width = 1000;
        int height = 800;
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // 设置白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 绘制标题
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 24));
        g2d.drawString("学习数据统计分析 - " + currentPeriod, 50, 50);

        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        g2d.setColor(Color.GRAY);
        g2d.drawString("生成时间: " + new java.util.Date(), 50, 80);
        g2d.drawString("学号：你的学号 姓名：你的姓名", 50, 100);

        // 绘制分隔线
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawLine(50, 110, width - 50, 110);

        // 绘制图表
        int chartY = 130;
        int chartWidth = 450;
        int chartHeight = 300;

        // 第一行：学习时长趋势和任务完成分析
        drawChartToImage(g2d, chart1Content, 50, chartY, chartWidth, chartHeight, "学习时长趋势");
        drawChartToImage(g2d, chart2Content, width/2 + 25, chartY, chartWidth, chartHeight, "任务完成分析");

        // 第二行：学科时间分布和专注度变化
        chartY += chartHeight + 50;
        drawChartToImage(g2d, chart3Content, 50, chartY, chartWidth, chartHeight, "学科时间分布");
        drawChartToImage(g2d, chart4Content, width/2 + 25, chartY, chartWidth, chartHeight, "专注度变化");

        g2d.dispose();
        return combinedImage;
    }

    private void drawChartToImage(Graphics2D g2d, JPanel chartContent, int x, int y, int width, int height, String title) {
        // 绘制图表标题
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("微软雅黑", Font.BOLD, 16));
        g2d.drawString(title, x, y - 10);

        // 绘制图表边框
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(x, y, width, height);

        // 创建图表的图片
        BufferedImage chartImage = new BufferedImage(chartContent.getWidth(), chartContent.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D chartGraphics = chartImage.createGraphics();
        chartContent.paint(chartGraphics);
        chartGraphics.dispose();

        // 将图表图片绘制到总图片中
        g2d.drawImage(chartImage, x + 10, y + 10, width - 20, height - 20, null);
    }

    // 数据模型类
    class ChartData {
        private double[] studyHours;
        private int[] taskCompletion;
        private double[] subjectDistribution;
        private double[] focusLevels;

        public ChartData(double[] studyHours, int[] taskCompletion, double[] subjectDistribution, double[] focusLevels) {
            this.studyHours = studyHours;
            this.taskCompletion = taskCompletion;
            this.subjectDistribution = subjectDistribution;
            this.focusLevels = focusLevels;
        }

        public double[] getStudyHours() { return studyHours; }
        public int[] getTaskCompletion() { return taskCompletion; }
        public double[] getSubjectDistribution() { return subjectDistribution; }
        public double[] getFocusLevels() { return focusLevels; }
    }
}