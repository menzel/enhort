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
public final class TrackBuilder {


    /**
     * Parses a command list and builds a track based on the command
     *
     * @param command - list of commands, a command is a track or a TrackBuilder.op (operator)
     * @return a new Track build by the command
     */
    private Track build(List<Object> command){

        return parse(command);
    }

    /**
     * Parses a command list and builds a track based on the command
     *
     * @param command - list of commands, a command is a track or a TrackBuilder.op (operator)
     *
     * @return a new Track build by the command
     * @throws IntervalTypeNotAllowedExcpetion
     */
    private Track parse(List<Object> command) {

        CopyOnWriteArrayList<Object> stack = new CopyOnWriteArrayList<>();
        int i = 0;


        while(stack.size() != 1 || i < command.size()){

            int top1 = stack.size()-1;
            int top2 = stack.size()-2;
            int top3 = stack.size()-3;


            //REDUCE (NOT)
            if (stack.size() >= 2 && stack.get(top1).equals(op.not) && stack.get(top2) instanceof Track) {
                stack.set(top1, Tracks.invert((Track) stack.get(top2)));
                stack.remove(top1);

                continue;
            }

            //REDUCE (ELSE)
            else if (stack.size() >= 3 && stack.get(top1) instanceof Track && stack.get(top3) instanceof Track) {
                if (stack.get(top2).equals(op.or))
                    stack.set(top3, Tracks.sum((Track) stack.get(top1), (Track) stack.get(top3)));
                else if (stack.get(top2).equals(op.and))
                    stack.set(top3, Tracks.intersect((Track) stack.get(top1), (Track) stack.get(top3)));
                else if (stack.get(top2).equals(op.xor))
                    stack.set(top3, Tracks.xor((Track) stack.get(top1), (Track) stack.get(top3)));
                stack.remove(top1);
                stack.remove(top2);

                continue;
            }

            else if(i >= command.size())
                continue;


            Object c = command.get(i++);

            //SHIFT (
            if (c.equals(op.lb)) {

                List<Object> subcommand = command.subList(command.indexOf(c) + 1, command.size());
                subcommand = subcommand.subList(0, subcommand.indexOf(op.rb));

                stack.add(parse(subcommand)); // recursive call to parse()

                i = command.indexOf(op.rb)+1; //set command pointer to the positions right after '('
            } else stack.add(c);


        }

        return (Track) stack.get(0);
    }

    /**
     * Translates a text command (e.g.: 1 and 2 or 3) in a list of operators
     *
     * @param command a command as a String with words for the operators
     *
     * @return  a list of commands, readable by the parse method
     */
    public Track build(String command){
        List<Object> cmd = new ArrayList<>();

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


        String desc = "A custom track create by the following expression: ";
        String ex = "";

        for(Object o: cmd){
            if(o instanceof Track)
                ex += ((Track) o).getName();
            else
                ex += o.toString().toUpperCase();
            ex += " ";
        }

        Track track = build(cmd);
        return TrackFactory.getInstance().createInOutTrack(track.getStarts(), track.getEnds(), ex , desc + ex, track.getAssembly());
    }

    private enum op {and, or , xor, not, lb, rb} //available operators

}
