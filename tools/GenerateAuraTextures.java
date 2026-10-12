import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/** Rebuilds the original pixel-art aura sprites without external art dependencies. */
public final class GenerateAuraTextures {
    private static final Path OUT = Path.of("resourcepack/assets/auracosmetics/textures/font");
    private static final Color GOLD = new Color(255, 174, 35);
    private static final Color PALE = new Color(255, 245, 187);
    private static final Color VIOLET = new Color(175, 55, 255);
    private static final Color PINK = new Color(255, 82, 236);

    public static void main(String[] args) throws IOException {
        Files.createDirectories(OUT);
        ring("gold_ring", GOLD, PALE, false);
        ring("violet_ring", VIOLET, PINK, true);
        spark("gold_spark", GOLD, PALE);
        spark("violet_spark", VIOLET, new Color(247, 195, 255));
        crystal();
        ribbon();
        fox();
        crown();
        flame();
    }

    private static BufferedImage image(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    private static Graphics2D graphics(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g;
    }

    private static Color alpha(Color c, int a) { return new Color(c.getRed(), c.getGreen(), c.getBlue(), a); }
    private static void save(String name, BufferedImage img) throws IOException {
        ImageIO.write(img, "png", OUT.resolve(name + ".png").toFile());
    }

    private static void glow(Graphics2D g, Shape path, Color color, Color core, float width) {
        g.setStroke(new BasicStroke(width + 12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(alpha(color, 18)); g.draw(path);
        g.setStroke(new BasicStroke(width + 6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(alpha(color, 48)); g.draw(path);
        g.setStroke(new BasicStroke(width + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(alpha(color, 180)); g.draw(path);
        g.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(core); g.draw(path);
    }

    private static void diamond(Graphics2D g, int x, int y, int r, Color color, Color core) {
        Polygon outer = new Polygon(new int[]{x, x+r, x, x-r}, new int[]{y-r, y, y+r, y}, 4);
        g.setColor(alpha(color, 48)); g.fillOval(x-r-5, y-r-5, 2*r+10, 2*r+10);
        g.setColor(color); g.fillPolygon(outer);
        Polygon inner = new Polygon(new int[]{x, x+r/2, x, x-r/2}, new int[]{y-r+2, y, y+r-2, y}, 4);
        g.setColor(core); g.fillPolygon(inner);
    }

    private static void ring(String name, Color color, Color core, boolean sigil) throws IOException {
        BufferedImage img = image(128, 128);
        Graphics2D g = graphics(img);
        for (int i = 0; i < 4; i++) {
            int inset = i * 3;
            glow(g, new Arc2D.Double(15+inset, 15+inset, 98-2*inset, 98-2*inset,
                    18+i*7, 315-i*13, Arc2D.OPEN), color, i == 0 ? core : color, i == 0 ? 2 : 1);
        }
        for (int i = 0; i < (sigil ? 12 : 8); i++) {
            double a = Math.PI * 2 * i / (sigil ? 12 : 8);
            int x = 64 + (int) Math.round(Math.cos(a) * 49);
            int y = 64 + (int) Math.round(Math.sin(a) * 49);
            diamond(g, x, y, sigil && i % 3 == 0 ? 5 : 3, color, core);
        }
        if (sigil) {
            glow(g, new Ellipse2D.Double(31, 31, 66, 66), color, core, 1);
            for (int i = 0; i < 4; i++) {
                double a = i * Math.PI / 2;
                int x = 64 + (int) (Math.cos(a) * 31);
                int y = 64 + (int) (Math.sin(a) * 31);
                diamond(g, x, y, 4, color, core);
            }
        }
        g.dispose(); save(name, img);
    }

    private static void spark(String name, Color color, Color core) throws IOException {
        BufferedImage img = image(32, 32);
        Graphics2D g = graphics(img);
        glow(g, new Line2D.Double(16, 3, 16, 29), color, core, 2);
        glow(g, new Line2D.Double(3, 16, 29, 16), color, core, 2);
        diamond(g, 16, 16, 4, color, core);
        g.dispose(); save(name, img);
    }

    private static void crystal() throws IOException {
        BufferedImage img = image(64, 64);
        Graphics2D g = graphics(img);
        Polygon body = new Polygon(new int[]{32, 46, 42, 32, 22, 18},
                new int[]{4, 19, 46, 59, 46, 19}, 6);
        g.setColor(alpha(VIOLET, 34)); g.fillOval(8, 2, 48, 60);
        g.setColor(alpha(VIOLET, 95)); g.fillOval(13, 5, 38, 54);
        g.setColor(new Color(120, 21, 197)); g.fillPolygon(body);
        g.setColor(new Color(215, 130, 255));
        g.fillPolygon(new int[]{32, 46, 32}, new int[]{4, 19, 59}, 3);
        g.setColor(new Color(250, 209, 255));
        g.fillPolygon(new int[]{32, 39, 32, 27}, new int[]{5, 20, 45, 20}, 4);
        glow(g, new Line2D.Double(32, 5, 32, 53), PINK, Color.WHITE, 1);
        g.dispose(); save("crystal", img);
    }

    private static void ribbon() throws IOException {
        BufferedImage img = image(32, 64);
        Graphics2D g = graphics(img);
        Path2D curve = new Path2D.Double();
        curve.moveTo(5, 62); curve.curveTo(29, 48, 2, 36, 27, 20);
        curve.curveTo(33, 14, 17, 7, 24, 2);
        glow(g, curve, VIOLET, new Color(252, 167, 255), 2);
        for (int i = 0; i < 6; i++) {
            int x = (i * 13 + 3) % 27 + 2;
            int y = (i * 19 + 11) % 58 + 3;
            g.setColor(i % 2 == 0 ? PINK : new Color(237, 181, 255));
            g.fillRect(x, y, 2 + i % 2, 2 + i % 2);
        }
        g.dispose(); save("ribbon", img);
    }

    private static void fox() throws IOException {
        BufferedImage img = image(192, 192);
        Graphics2D g = graphics(img);
        // Five luminous tail plumes make the silhouette read from a distance.
        for (int i = 0; i < 5; i++) {
            Path2D tail = new Path2D.Double();
            tail.moveTo(85, 119);
            tail.curveTo(120+i*5, 138-i*13, 164+i*3, 139-i*24, 168-i*6, 56-i*8);
            tail.curveTo(182-i*4, 89-i*11, 187-i*3, 141-i*12, 131, 155-i*5);
            glow(g, tail, new Color(255, 113+i*12, 20), i % 2 == 0 ? PALE : GOLD, 3+i%2);
        }
        Path2D head = new Path2D.Double();
        head.moveTo(16, 57); head.lineTo(39, 69); head.lineTo(50, 34);
        head.lineTo(65, 58); head.lineTo(86, 46); head.lineTo(80, 84);
        head.curveTo(106, 99, 101, 124, 77, 132);
        head.curveTo(50, 139, 37, 111, 18, 103);
        head.lineTo(42, 92); head.closePath();
        g.setColor(alpha(GOLD, 30)); g.setStroke(new BasicStroke(16)); g.draw(head);
        g.setColor(new Color(255, 122, 24)); g.fill(head);
        Path2D muzzle = new Path2D.Double();
        muzzle.moveTo(18, 103); muzzle.lineTo(44, 91); muzzle.lineTo(79, 97);
        muzzle.curveTo(71, 119, 48, 121, 18, 103); muzzle.closePath();
        g.setColor(PALE); g.fill(muzzle);
        g.setColor(new Color(255, 231, 167));
        g.fillPolygon(new int[]{48, 52, 63}, new int[]{45, 70, 68}, 3);
        g.fillPolygon(new int[]{20, 41, 38}, new int[]{63, 75, 82}, 3);
        g.setColor(new Color(91, 23, 18)); g.fillRect(52, 78, 9, 5);
        g.setColor(Color.WHITE); g.fillRect(58, 78, 3, 3);
        g.setColor(new Color(101, 37, 19)); g.fillRect(19, 99, 6, 4);
        // Fire embers are painted into the texture, so no vanilla particles are used.
        for (int i = 0; i < 24; i++) {
            int x = (i * 67 + 17) % 180 + 6;
            int y = (i * 43 + 9) % 177 + 7;
            g.setColor(i % 3 == 0 ? PALE : GOLD);
            g.fillRect(x, y, i % 5 == 0 ? 4 : 2, i % 5 == 0 ? 4 : 2);
        }
        g.dispose(); save("fox", img);
    }

    private static void crown() throws IOException {
        BufferedImage img = image(64, 64);
        Graphics2D g = graphics(img);
        Path2D crown = new Path2D.Double();
        crown.moveTo(8, 22); crown.lineTo(20, 36); crown.lineTo(31, 13);
        crown.lineTo(43, 36); crown.lineTo(56, 22); crown.lineTo(51, 48);
        crown.lineTo(13, 48); crown.closePath();
        g.setColor(alpha(GOLD, 45)); g.setStroke(new BasicStroke(12)); g.draw(crown);
        g.setColor(new Color(255, 174, 35)); g.fill(crown);
        g.setColor(PALE); g.setStroke(new BasicStroke(3)); g.draw(crown);
        diamond(g, 32, 13, 4, GOLD, Color.WHITE);
        diamond(g, 9, 21, 3, GOLD, PALE);
        diamond(g, 55, 21, 3, GOLD, PALE);
        g.dispose(); save("crown", img);
    }

    private static void flame() throws IOException {
        BufferedImage img = image(48, 64);
        Graphics2D g = graphics(img);
        Path2D flame = new Path2D.Double();
        flame.moveTo(24, 61); flame.curveTo(0, 52, 7, 37, 19, 25);
        flame.curveTo(17, 36, 30, 20, 27, 4);
        flame.curveTo(52, 25, 51, 51, 24, 61); flame.closePath();
        g.setColor(alpha(GOLD, 28)); g.setStroke(new BasicStroke(10)); g.draw(flame);
        g.setColor(new Color(255, 96, 13)); g.fill(flame);
        Path2D core = new Path2D.Double();
        core.moveTo(24, 56); core.curveTo(13, 45, 20, 38, 28, 27);
        core.curveTo(36, 41, 37, 49, 24, 56); core.closePath();
        g.setColor(PALE); g.fill(core);
        g.dispose(); save("flame", img);
    }
}
