package de.thm.misc;

import de.thm.exception.IntervalTypeNotAllowedExcpetion;
import de.thm.genomeData.Track;
import de.thm.genomeData.TrackFactory;
import de.thm.genomeData.Tracks;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The TrackBuilder class offers methods to generate a track based on a logical expression using a SHIFT/ REDUCE Parser
 *
 * Created by menzel on 10/11/16.
 */
public class TrackBuilder {
    public Track build(List<Object> command){


        try {
            return parse(command);
        } catch (IntervalTypeNotAllowedExcpetion intervalTypeNotAllowedExcpetion) {
            intervalTypeNotAllowedExcpetion.printStackTrace();
        }

        return null;
    }

    private Track parse(List<Object> command) throws IntervalTypeNotAllowedExcpetion {

        CopyOnWriteArrayList<Object> stack = new CopyOnWriteArrayList<>();
        int i = 0;


        while(stack.size() != 1 || i < command.size()){


            //REDUCE (NOT)
            if (stack.size() == 2 && stack.get(0).equals(op.not) && stack.get(1) instanceof Track) {
                stack.set(0, Tracks.invert((Track) stack.get(1)));
                stack.remove(1);

                continue;
            }

            //REDUCE (ELSE)
            else if (stack.size() == 3 && stack.get(0) instanceof Track && stack.get(2) instanceof Track) {
                if (stack.get(1).equals(op.and))
                    stack.set(0, Tracks.sum((Track) stack.get(0), (Track) stack.get(2)));
                else if (stack.get(1).equals(op.or))
                    stack.set(0, Tracks.intersect((Track) stack.get(0), (Track) stack.get(2)));
                else if (stack.get(1).equals(op.xor))
                    stack.set(0, Tracks.xor((Track) stack.get(0), (Track) stack.get(2)));
                stack.remove(1);
                stack.remove(1);

                continue;
            }

            else if(i >= command.size())
                continue;


            Object c = command.get(i++);

            //SHIFT
            if (c.equals(op.lb)) {
                // call parse with subcommand

                List<Object> subcommand = command.subList(command.indexOf(c) + 1, command.size());
                subcommand = subcommand.subList(0, subcommand.indexOf(op.rb));

                stack.add(parse(subcommand));

                i = command.indexOf(op.rb)+1;
            } else stack.add(c);


        }

        return (Track) stack.get(0);
    }

    public Track build(String command){
        List<Object> cmd = new ArrayList<>();

        command = "(" + command + ")";

        command = command.replace("(", " ( ");
        command = command.replace(")", " ) " );

        TrackFactory factory = TrackFactory.getInstance();

        for(String part: command.split(" ")){
            if(part.contentEquals("and"))
                cmd.add(op.and);
            else if(part.contentEquals("or"))
                cmd.add(op.or);
            else if(part.contentEquals("xor"))
                cmd.add(op.xor);
            else if(part.contentEquals("not"))
                cmd.add(op.not);
            else if(part.contentEquals("("))
                cmd.add(op.lb);
            else if(part.contentEquals(")"))
                cmd.add(op.rb);
            else if(NumberUtils.isNumber(part)){
                cmd.add(factory.getTrackById(Integer.parseInt(part)));
            }
        }


        return build(cmd);
    }


    enum op {and, or , xor, not, lb, rb, root}

}
