package com.wood9366.game.tool.research;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResearchFont {
	static public void main(String[] args) {
		String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		
		System.out.println("Available Fonts:");
		for (String fontName : fontNames) {
			System.out.println(fontName);
		}
		
		Font f = new Font("Wingdings", Font.BOLD, 200);
		
		BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = (Graphics2D)img.getGraphics();
		
		RenderingHints rh = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		//rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.setRenderingHints(rh);
		
		g.setFont(f);
		g.setColor(Color.black);
		
		String t = "你";
		
		GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), t);
		Rectangle r = gv.getPixelBounds(null, 0, 0);
		
		g.drawString(t, (int)-r.getX(), (int)-r.getY());
		
		printStringInfo(g, t);
		
		try {
			ImageIO.write(img, "png", new File("f.png"));
		} catch (IOException e) {
			
		}
	}
	
	static private void printStringInfo(Graphics2D g, String str) {
		GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), str);
		GlyphMetrics gm = gv.getGlyphMetrics(0);
		
		System.out.println(str + " font graphic info:");
		System.out.println("visual bound: " + gv.getVisualBounds().toString());
		System.out.println("pixel rect: " + gv.getPixelBounds(null, 0, 0).toString());
		System.out.println("bound 2d: " + gm.getBounds2D().toString());
		System.out.println("advance: " + Float.toString(gm.getAdvance()));
		System.out.println("advance x: " + Float.toString(gm.getAdvanceX()));
		System.out.println("advance y: " + Float.toString(gm.getAdvanceY()));
		System.out.println("lsb: " + Float.toString(gm.getLSB()));
		System.out.println("rsb: " + Float.toString(gm.getRSB()));
	}
}