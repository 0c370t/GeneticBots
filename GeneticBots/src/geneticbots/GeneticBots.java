package geneticbots;


import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class GeneticBots
{
    protected static int fps = 60;
    protected static final int frames = 300;
    protected static int population = 100;
    protected static int barriers = 6;
    protected static Target target;
    protected static int length=600;
    protected static int width=900;
    protected static double mutation = .001;
    protected static List<Barrier> barrierArray;
    protected static List<Physics> physics;
    protected static List<Kid> kids;
    protected static List<Actor> actors;
    protected static int generation;
    protected static int sx = 100, sy = 500; //start x and y
    static boolean start = false;
    protected static Rectangle screen;
    public static void main(String[] args)
    {
        Properties.main(args);
        while (!start)
        {
            System.out.print("");
        }
        JFrame holder = new JFrame("GeneticBots");
        screen = new Rectangle(0,0, holder.getWidth(), holder.getHeight());
        Drawer theDrawer = new Drawer();
        holder.setVisible(false);
        JSlider slideMutation = new JSlider();
        slideMutation.setBounds(125, 35, 150, 20);
        slideMutation.setValue((int)(mutation*1000));
        slideMutation.setMaximum(1000);
        slideMutation.setMinimum(0);
        slideMutation.setVisible(true);
        holder.add(slideMutation);
        JSlider slideFrames = new JSlider();
        slideFrames.setBounds(125, 25, 150, 20);
        slideFrames.setValue(fps);
        slideFrames.setMaximum(300);
        slideFrames.setMinimum(1);
        slideFrames.setVisible(true);
        holder.add(slideFrames);
        holder.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        holder.setBounds(0,0,width,length);
        final JPanel homePanel = new JPanel();
        holder.getContentPane().add(theDrawer);
        barrierArray = new ArrayList();
        physics = new ArrayList();
        kids = new ArrayList();
        actors = new ArrayList();
        
        target = new Target((width-60), (length / 2));
        actors.add(target);
        for (int k = 1; k <= barriers; k++)
        {
            actors.add(new Barrier((int)(Math.random()*(width-15)),(int)(Math.random()*length)-20,(int)(Math.random()*90)+50,(int)(Math.random()*60)+30));
            barrierArray.add((Barrier) actors.get(k));
            if (actors.get(k).contains(new Rectangle(sx-50,sy-50, sx+50, sy+50)) || 
                    actors.get(k).contains(new Rectangle(0, 0, 70, 50))||
                    actors.get(k).contains(target))
            {
                k--;
            }
        }
        
        double[][] temp;
        for (int k = 0; k < population; k++)
        {
            temp = new double[3][3];

            for (int i = 0; i < 3; i++)
            {
                temp[0][i] = (Math.random() * 2 * Math.PI);
            }
            for (int i = 0; i < 3; i++)
            {
                temp[1][i] =  Math.random() * 1;
            }
            double sum=0;
            for(int i=0;i<3;i++)
            {
                temp[2][i] = Math.random();
                sum+=temp[2][i];
            }
            for(int i=0;i<3;i++)
            {
                temp[2][i]/=sum;
            }
            
            kids.add(new Kid(sx, sy, temp, new Color(  
                            (int) (Math.random() * 255) + 1, 
                            (int) (Math.random() * 255) + 1, 
                            (int) (Math.random() * 255) + 1)));
            actors.add(kids.get(k));
            for (Kid phy : kids)
            {
                physics.add(phy);
            }
        }
        
        holder.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent me)
            {
                
            }

            public void mousePressed(MouseEvent me)
            {
                //if(me.getX())
            }

            public void mouseReleased(MouseEvent me)
            {
                
            }

            public void mouseEntered(MouseEvent me)
            {
                
            }

            public void mouseExited(MouseEvent me)
            {
                
            }
        });
        holder.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent me)
            {
                
            }

            public void mouseMoved(MouseEvent me)
            {
                
            }
        });
        
        holder.setVisible(true);
        holder.repaint();
        while(true)
        {
            for (int k = 0; k < frames; k++)
            {
                screen.setSize(holder.getWidth(), holder.getHeight());
                move();
                holder.repaint();
                //System.out.println(k);
                try
                {
                    Thread.sleep(1000/fps);
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex);
                }
                fps=slideFrames.getValue();
                mutation=slideMutation.getValue()/1000.0;
                
            }
            kids = nextGen();
            for(int i=barriers;i<population+barriers;i++)
            {
                actors.set(i, kids.get(i-barriers));
            }
            for (Kid phy : kids)
            {
                physics.add(phy);
            }
            generation++;
        }
        
    }
    
    public static int getGenerations()
    {
        return generation;
    }
    
    public static List<Actor> getActors()
    {
        return actors;
    }
    
    public static List<Kid> nextGen()
    {
        List<Kid> newKids = new ArrayList();
        int total=0;
        for(int i=0;i<kids.size();i++)
        {
            kids.get(i).setFit(Math.sqrt(Math.pow(kids.get(i).getX()-target.getX(), 2)+Math.pow(kids.get(i).getY()-target.getY(),2)));
            total += kids.get(i).getFit();
        }
        
        double sum = 0;
        
        for (int k = 0; k < kids.size(); k++)
        {
            kids.get(k).setFit(total / kids.get(k).getFit());
            sum += kids.get(k).getFit();
        }
        
        double sum2=0;
        for (int k = 0; k < kids.size(); k++)
        {
            sum2+=kids.get(k).getFit() / sum;
            kids.get(k).setFit(sum2);
        }
        
        double rand;
        Kid[] temp = new Kid[2];
        for(int i=0;i<population;i++)
        {
            for(int j=0;j<2;j++)
            {
                rand=Math.random();
                for(int k=0;k<population-1;k++)
                {
                    if(kids.get(k).getFit() > rand)
                    {
                        temp[j] = kids.get(k);
                        break;
                    }
                    temp[j]=kids.get(kids.size()-1);
                }
            }
            newKids.add(makeBaby(temp[0], temp[1]));
        }
        return newKids;
    }
    
    public static Kid makeBaby(Kid parent1, Kid parent2)
    {
        double[][] p1 = parent1.getGenes();
        double[][] p2 = parent2.getGenes();
        double[][] kid = new double[p1.length][p1[0].length];
       
        for (int j = 0; j < 3; j++)
        {
            kid[0][j] = (p1[0][j] + p2[0][j]) / 2;
            if (Math.random() < mutation)
            {
                kid[0][j] = Math.random() * 360;
            }
        }
        
        for (int j = 0; j < 3; j++)
        {
            kid[1][j] = (p1[1][j] + p2[1][j]) / 2;
            if (Math.random() < mutation)
            {
                kid[1][j] = Math.random();
            }
        }
        double sum = 0;
        double te;
        double[] ti = {-1, -1, -1};
        for (int j = 0; j < 3; j++)
        {
            if (Math.random() < mutation)
            {
                te = kid[1][j] = Math.random();
                ti[j] = te;
            }
            else
            {
                te = (p1[2][j] + p2[2][j]) / 2;
            }
            sum += te;
        }
        for (int j = 0; j < 3; j++)
        {
            if (ti[j] != -1)
            {
                te = ti[j];
            }
            else
            {
                te = (p1[2][j] + p2[2][j]) / sum;
            }
            kid[2][j] = te;
            
        }
        
        Color c;
        if (Math.random() < mutation)
        {
            c = new Color(  (int) (Math.random() * 255) + 1, 
                            (int) (Math.random() * 255) + 1, 
                            (int) (Math.random() * 255) + 1);
        }
        else
        {
            if (Math.random() > .5)
            {
                c = parent1.color;
            }
            else
            {
                c = parent2.color;
            }
        }
        
        return new Kid(sx, sy, kid, c);
    }
    
    public static void move()
    {
        for(int i=0;i<physics.size();i++)
        {
            physics.get(i).move(barrierArray);
        }
    }

    public static void setPopulation(int population)
    {
        GeneticBots.population = population;
    }

    public static void setBarriers(int barriers)
    {
        GeneticBots.barriers = barriers;
    }

    public static void setMutation(double mutation)
    {
        GeneticBots.mutation = mutation;
    }
    
    public static String getStats(int i)
    {
        
        String[] out = {"Generation:    " + GeneticBots.getGenerations(),
                        "Population:    " + (actors.size() - barriers - 1),
                        "Frames/Second: " + fps,
                        "Mutation Rate: " + mutation};
        return out[i];
    }

    public static void setFps(int fps)
    {
        GeneticBots.fps = fps;
    }
    
}
