import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.text.DecimalFormat;

@SuppressWarnings("serial")
public class paint extends JFrame{
	JButton brushbutton, linebutton, elipsbutton, recbutton, bordbutton, fillbutton;
	JSlider transSlider;
	JLabel transLabel;
	DecimalFormat dec = new DecimalFormat("#.##");
	
	Graphics2D graphSettings;
	int movecount = 1;
	float transparantValue = 1.0F;
	Color bordcolor = Color.BLACK, fillcolor = Color.black;
	public static void main(String[] args) {
		new paint();
	}
	public paint() {//constructer(program baþladýðýnda aktifleþen ilk method)
		this.setSize(1000,700);
		this.setTitle("Paint With Java");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//Programdan çýkmak için kullanýlan button oluþumu
		JPanel buttonPanel = new JPanel();//Buttonlar için tanýmlanan özel alan
		Box box = Box.createHorizontalBox();
		//brushbutton, linebutton, elipsbutton, recbutton, bordbutton, fillbutton
		brushbutton = ButtonMethod("./brush.png",1);
		linebutton = ButtonMethod("./line.png",2);
		elipsbutton = ButtonMethod("./elips.png",3);
		recbutton = ButtonMethod("./rec.png",4);
		bordbutton = ColorButtonMethod("./bord.png",5,true);
		fillbutton = ColorButtonMethod("./stroke.png",6,false);
		
		/*Butonlar daha önce tanýmlanmýþ olan kutuya eklenir*/
		box.add(brushbutton);
		box.add(linebutton);
		box.add(elipsbutton);
		box.add(recbutton);
		box.add(bordbutton);
		box.add(fillbutton);
		
		transLabel = new JLabel("Transparant: 1");
		transSlider = new JSlider(1, 99, 99);
		ListenForSlider sliderL = new ListenForSlider();
		
		transSlider.addChangeListener(sliderL);
		box.add(transLabel);
		box.add(transSlider);
		
		/*Butonlarýn ekllendiði kutu panele eklenir*/
		buttonPanel.add(box);
		
		this.add(buttonPanel, BorderLayout.SOUTH);
		this.add(new drawPaper(), BorderLayout.CENTER);
		this.setVisible(true);
		
	}
	public JButton ButtonMethod(String iconFile, final int moveclicknum) {
		JButton buttonJ = new JButton();
		Icon buttonIcon = new ImageIcon(iconFile);
		buttonJ.setIcon(buttonIcon);
		
		buttonJ.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				movecount = moveclicknum;
				
			}});
		return buttonJ;
	}
	
	public JButton ColorButtonMethod(String iconFile, final int moveclicknum, final boolean borderline) {
		JButton buttonJ = new JButton();
		Icon buttonIcon = new ImageIcon(iconFile);
		buttonJ.setIcon(buttonIcon);
		
		buttonJ.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(borderline) {
					 bordcolor = JColorChooser.showDialog(null, "Choose a borderline color", Color.BLACK);
				}
				else {
					fillcolor = JColorChooser.showDialog(null,"Choose a filling color", Color.BLACK);
				}
				
			}});
		return buttonJ;
	}
	private class drawPaper extends JComponent{
		ArrayList<Shape> shapes = new ArrayList<Shape>();
		ArrayList<Color> fillshapes = new ArrayList<Color>();
		ArrayList<Color> bordshapes = new ArrayList<Color>();
		ArrayList<Float> transparantPercent = new ArrayList<Float>();
		Point drawStart, drawEnd;
		public drawPaper()
		{
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					if(movecount != 1) {
						drawStart = new Point(e.getX(), e.getY());
						drawEnd = drawStart;
						repaint();
					}
				}
				public void mouseReleased(MouseEvent e) {
					if(movecount != 1) {
						Shape shape = null;
						if(movecount == 2) {
							shape =  lineDrawing(drawStart.x, drawStart.y, e.getX(), e.getY());
						}else 
							if(movecount == 3) {
								shape =  elipsDrawing(drawStart.x, drawStart.y, e.getX(), e.getY());
							}else 
								if(movecount == 4){
								shape = rectangleDrawing(drawStart.x, drawStart.y, e.getX(), e.getY());
							}
						/*shape = rectangleDrawing(drawStart.x, drawStart.y, e.getX(), e.getY());*/ 
						shapes.add(shape);
						fillshapes.add(fillcolor);
						bordshapes.add(bordcolor);
					
						transparantPercent.add(transparantValue);
					
						drawStart = null;
						drawEnd = null;
						repaint();
					}
				}
				
			});
			this.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseDragged(MouseEvent e) {
					if(movecount == 1) {
						int x = e.getX();
						int y = e.getY();
						Shape shape = null;
					
						bordcolor = fillcolor;
					
						shape = brushDrawing(x, y, 5, 5);
						shapes.add(shape);
						fillshapes.add(fillcolor);
						bordshapes.add(bordcolor);
						transparantPercent.add(transparantValue);
					}
					drawEnd = new Point(e.getX(), e.getY());
					repaint();
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
			});
		}
		public void paint(Graphics g) {
			graphSettings = (Graphics2D) g;
			graphSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphSettings.setStroke(new BasicStroke(4));
			
			Iterator<Color> bordNumber = bordshapes.iterator();
			Iterator<Color> fillNumber = fillshapes.iterator();
			Iterator<Float> transcount = transparantPercent.iterator();
			
			//graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));
			
			for(Shape s: shapes) {
				graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transcount.next()));
				graphSettings.setPaint(bordNumber.next());
				graphSettings.draw(s);
				graphSettings.setPaint(fillNumber.next());
				graphSettings.fill(s);
				
			}
			if(drawStart!=null && drawEnd!=null) {
				graphSettings.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.40f));
				graphSettings.setPaint(Color.LIGHT_GRAY);
				
				Shape shape = null;
				if(movecount == 2) {
					shape = lineDrawing(drawStart.x,drawStart.y, drawEnd.x, drawEnd.y);
					
				}else 
					if(movecount == 3) {
						shape = elipsDrawing(drawStart.x,drawStart.y, drawEnd.x, drawEnd.y);
						
					}else 
						if(movecount == 4) {
							shape = rectangleDrawing(drawStart.x,drawStart.y, drawEnd.x, drawEnd.y);
						}
				graphSettings.draw(shape);
			}
				//shape = rectangleDrawing(drawStart.x,drawStart.y, drawEnd.x, drawEnd.y);
				
		}
			
	
	private Rectangle2D.Float rectangleDrawing(int x1, int y1, int x2, int y2){
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		
		int width = Math.abs(x1-x2);
		int height = Math.abs(y1-y2);
		
		return new Rectangle2D.Float(x,y,width, height);
	}
	
	private Ellipse2D.Float elipsDrawing(int x1, int y1, int x2, int y2){
		int x = Math.min(x1, x2);
		int y = Math.min(y1, y2);
		
		int width = Math.abs(x1-x2);
		int height = Math.abs(y1-y2);
		
		return new Ellipse2D.Float(x,y,width, height);
	}
	private Line2D.Float lineDrawing(int x1, int y1, int x2, int y2){
		return new Line2D.Float(x1,y1,x2,y2);
	}
	private Ellipse2D.Float brushDrawing(int x1, int y1, int brushBordWidth, int brushBordHeight){
		return new Ellipse2D.Float(x1,y1, brushBordWidth, brushBordHeight);
	}
	}
	
	
	private  class ListenForSlider implements ChangeListener{
		
		@Override
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() == transSlider) {
				transLabel.setText("Transparant "+ dec.format(transSlider.getValue() * .01));//slider deðerlerini duruma göre deðiþtirmek için kullanýlacak olan line
				
			}
			transparantValue = (float) (transSlider.getValue() * .01);
			
		}
		
	}
}
