package com.wood9366.game.tool.fontexporter;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.wood9366.game.binpacker.BinData;
import com.wood9366.game.binpacker.BinPacker;
import com.wood9366.game.binpacker.ISpriteData;
import com.wood9366.game.binpacker.Rect;

public class FontExporter extends JFrame {
	private static final long serialVersionUID = 1L;
	
	class FontData implements ISpriteData {
		public FontData(Font f, char c) {
			_f = f;
			_c[0] = c;
			
			BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = g2d(img);
			GlyphVector gv = g.getFont().createGlyphVector(g.getFontRenderContext(), _c);
			Rectangle r = gv.getPixelBounds(null, 0, 0);
			
			_r = Rect.Create((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
		}
		
		@Override
		public int border() {
			return 1;
		}

		@Override
		public Rect rect() {
			return Rect.Create(0, 0, rectContent().width() + border() * 2, rectContent().height() + border() * 2);
		}

		@Override
		public Rect rectContent() {
			return Rect.Create(0, 0, _r.width(), _r.height());
		}

		@Override
		public int width() {
			return rectContent().width();
		}

		@Override
		public int height() {
			return rectContent().height();
		}

		@Override
		public String name() {
			return "" + _c[0];
		}

		@Override
		public BufferedImage image() {
			BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = g2d(img);

			g.drawChars(_c, 0, 1, -_r.left(), -_r.top());
			
			return img.getSubimage(0, 0, _r.width(), _r.height());
		}

		@Override
		public String info() {
			return "" + _c[0];
		}
		
		private Graphics2D g2d(BufferedImage img) {
			Graphics2D g = (Graphics2D)img.getGraphics();
			
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			g.setRenderingHints(rh);
			
			g.setFont(_f);
			g.setColor(Color.white);
			
			return g;
		}
		
		private Rect _r = null;
		private Font _f = null;
		private char[] _c = new char[] {'#'};
	}
	
	class JFontDisplayPanel extends JPanel implements MouseListener, MouseWheelListener, MouseMotionListener {
		private static final long serialVersionUID = 1L;

		public JFontDisplayPanel() {
			addMouseListener(this);
			addMouseWheelListener(this);
			addMouseMotionListener(this);
			
			_image = createCheckerImage(2048, 2048);
			
			setBackground(Color.gray);
		}
		
	    @Override
	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        
	        Graphics2D g2d = (Graphics2D) g;
	        
	        g2d.setColor(getBackground());
	        g2d.fillRect(0, 0, getWidth(), getHeight());
	        
	        int x = Math.round(_x);
	        int y = Math.round(_y);
	        int w = Math.round(_image.getWidth() * _scale);
	        int h = Math.round(_image.getHeight() * _scale);
	        
	        g2d.drawImage(_image, x, y, w, h, null);
	        
	        // draw image outline
	        g2d.setColor(Color.black);
	        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1, 
	        		new float[] {5,5}, 0));
	        
	        g2d.drawRect(x, y, w, h);
	    }
	    
	    public void changeImage(BufferedImage img) {
	    	_image = img;
	    	repaint();
	    }
	    
	    public void fit() {
	    	float sx = (float)getWidth() / (_image.getWidth() * 1.1f);
	    	float sy = (float)getHeight() / (_image.getHeight() * 1.1f);

	    	_scale = Math.min(sx, sy);
	    	_x = (getWidth() - _image.getWidth() * _scale) / 2;
	    	_y = (getHeight() - _image.getHeight() * _scale) / 2;
	    	
	    	repaint();
	    }

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			float scaleSrollSpeed = 0.01f;
			float prevScale = _scale;
			
			_scale += -e.getWheelRotation() * scaleSrollSpeed;
			_scale = Math.min(10, _scale);
			_scale = Math.max(0.1f, _scale);

			_x = e.getX() + (_x - e.getX()) * _scale / prevScale;
			_y = e.getY() + (_y - e.getY()) * _scale / prevScale;
			
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			_draging = true;
			_dragStartPoint.setLocation(e.getPoint());
			_dragStartPos.setLocation(_x, _y);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (_draging) {
				_draging = false;
			}
		}
		
		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (_draging) {
				_x = (int)(_dragStartPos.getX() + e.getX() - _dragStartPoint.getX());
				_y = (int)(_dragStartPos.getY() + e.getY() - _dragStartPoint.getY());
				repaint();
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
		
		private BufferedImage createCheckerImage(int width, int height) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			
			for (int x = 0; x < width; x++) {
	    		for (int y = 0; y < height; y++) {
	    			int c = ((int)(x / 16));
	    			int r = ((int)(y / 16));
	    			
	    			boolean white = false;
	    			
	    			if (r % 2 == 0) {
	    				white = (c % 2) == 0;
	    			} else {
	    				white = (c % 2) == 1;
	    			}
	    			
	    			image.setRGB(x, y, white ? 0xffffffff : 0xff000000);
	    		}
	    	}
			
			return image;
		}
		
		private Point _dragStartPos = new Point();
		private Point _dragStartPoint = new Point();
		private boolean _draging = false;
		
		private float _x = 0;
		private float _y = 0;
		private float _scale = 1.0f;
		private BufferedImage _image = null;
	}
	
	public FontExporter() {
		init();
		initUI();
	}
	
	private void initUI() {
		_panelFontDisplay = new JFontDisplayPanel();

		_panelFontDisplay.setPreferredSize(new Dimension(400, 400));
		add(_panelFontDisplay, BorderLayout.CENTER);
		
		JList<String> listFonts = new JList<String>(_fontNames);
		
		listFonts.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					changeFont(listFonts.getSelectedValue(), 30);
				}
			}
		});
		
		JPanel right = new JPanel();
		
		right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));
		
		JPanel controls = new JPanel();
		controls.setPreferredSize(new Dimension(200, 0));
		
		_cbBins = new JComboBox<String>();
		_cbBins.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					String val = e.getItem().toString();
					
					if (_binImages.containsKey(val)) {
						BufferedImage img = _binImages.get(val);
						
						_lbSize.setText(String.format("%d x %d", img.getWidth(), img.getHeight()));
						_panelFontDisplay.changeImage(img);
					}
				}
			}
		});
		
		_lbSize = new JLabel("Size: ");
		
		JScrollPane paneTextContent = new JScrollPane();
		paneTextContent.setPreferredSize(new Dimension(200, 300));
		
		JTextArea textContent = new JTextArea();
		textContent.setLineWrap(true);
		textContent.setText(_content);
		
		paneTextContent.getViewport().add(textContent);
		
		JButton btPack = new JButton("Pack");
		
		btPack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				changeContent(textContent.getText());
			}
		});
		
		controls.add(_cbBins);
		controls.add(_lbSize);
		controls.add(paneTextContent);
		controls.add(btPack);
		
		JScrollPane paneFontLists = new JScrollPane();
		
		paneFontLists.getViewport().setView(listFonts);
		
		right.add(controls);
		right.add(paneFontLists);
		
		add(right, BorderLayout.EAST);
		
		pack();
		
		setTitle("Font Exporter");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private void changeContent(String content) {
		_content = content;
		repack();
		_panelFontDisplay.fit();
	}
	
	private void changeFont(String fontName, int size) {
    	_font = new Font(fontName, Font.PLAIN, size);
    	repack();
    	_panelFontDisplay.fit();
    }
	
	private void repack() {
    	// filter char list from content
    	Set<Character> chars = new HashSet<Character>();
    	
    	for (int i = 0; i < _content.length(); i++) {
    		Character c = _content.charAt(i);
    		
    		if (!Character.isWhitespace(c)) {
    			chars.add(c);
    		}
    	}
    	
    	BinPacker p = new BinPacker();
    	
    	for (char c : chars) {
    		p.addImage(new FontData(_font, c));
    	}
    	
    	p.pack();
    	
    	_binImages.clear();
    	
    	for (BinData bin : p.bins()) {
    		_binImages.put(Integer.toString(bin.id()), bin.packedImage());
    	}
    	
    	_cbBins.removeAllItems();
    	
    	for (String id : _binImages.keySet()) {
    		_cbBins.addItem(id);
    	}
    }
	
	private Font _font = null;
	private String _content = "abc";
	private Map<String, BufferedImage> _binImages = new HashMap<String, BufferedImage>();
	
	private JLabel _lbSize = null;
	private JComboBox<String> _cbBins = null;
	private JFontDisplayPanel _panelFontDisplay = null;
	
	private void init() {
		_fontNames.clear();
		
		gatherSystemFonts();
	}
	
	private void gatherSystemFonts() {
		GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		for (String fontName : g.getAvailableFontFamilyNames()) {
			_fontNames.addElement(fontName);
		}
	}
	
	private DefaultListModel<String> _fontNames = new DefaultListModel<String>();
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				FontExporter exporter = new FontExporter();
				exporter.setVisible(true);
			}
		});
	}

}
