import java.awt.*;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class LeftLeaningRedBlackTreeVisualization {

    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int NODE_RADIUS = 20;
    private static final int VERTICAL_SPACING = 60;

    private static DrawingPanel panel;
    private static Graphics g;

    public static void main(String[] args) throws InterruptedException {
        panel = new DrawingPanel(PANEL_WIDTH, PANEL_HEIGHT);
        g = panel.getGraphics();

        Scanner scanner = new Scanner(System.in);
        LLRBTree tree = new LLRBTree();

        System.out.println("Enter integers to insert into the tree (enter 'done' to finish):");
        while (true) {
            String input = scanner.nextLine();
            if ("done".equalsIgnoreCase(input)) break;

            try {
                int value = Integer.parseInt(input);
                tree.insert(value);
                animate(() -> drawTree(tree.root, PANEL_WIDTH / 2, 50, PANEL_WIDTH / 4));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer or 'done'.");
            }
        }
    }

    private static void animate(Runnable drawAction) throws InterruptedException {
        panel.clear();
        drawAction.run();
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    private static void drawTree(TreeNode node, int x, int y, int offset) {
        if (node == null) return;

        if (node.left != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x - offset, y + VERTICAL_SPACING);
            drawTree(node.left, x - offset, y + VERTICAL_SPACING, offset / 2);
        }

        if (node.right != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x + offset, y + VERTICAL_SPACING);
            drawTree(node.right, x + offset, y + VERTICAL_SPACING, offset / 2);
        }

        g.setColor(node.isRed ? Color.RED : Color.BLACK);
        g.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(node.value), x - 5, y + 5);
    }

    static class LLRBTree {
        TreeNode root;

        public void insert(int value) throws InterruptedException {
            root = insert(root, value);
            root.isRed = false;
        }

        private TreeNode insert(TreeNode h, int value) throws InterruptedException {
            if (h == null) return new TreeNode(value, true);

            if (value < h.value) h.left = insert(h.left, value);
            else if (value > h.value) h.right = insert(h.right, value);

            if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
            if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
            if (isRed(h.left) && isRed(h.right)) flipColors(h);

            animate(() -> drawTree(root, PANEL_WIDTH / 2, 50, PANEL_WIDTH / 4));
            return h;
        }

        private TreeNode rotateLeft(TreeNode h) {
            TreeNode x = h.right;
            h.right = x.left;
            x.left = h;
            x.isRed = h.isRed;
            h.isRed = true;
            return x;
        }

        private TreeNode rotateRight(TreeNode h) {
            TreeNode x = h.left;
            h.left = x.right;
            x.right = h;
            x.isRed = h.isRed;
            h.isRed = true;
            return x;
        }

        private void flipColors(TreeNode h) {
            h.isRed = !h.isRed;
            h.left.isRed = !h.left.isRed;
            h.right.isRed = !h.right.isRed;
        }

        private boolean isRed(TreeNode node) {
            return node != null && node.isRed;
        }
    }

    static class TreeNode {
        int value;
        boolean isRed;
        TreeNode left, right;

        TreeNode(int value, boolean isRed) {
            this.value = value;
            this.isRed = isRed;
        }
    }
}