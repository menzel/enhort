package de.thm.result;


import de.thm.misc.Genome;

import java.io.Serializable;

public interface Result extends Serializable {

    Genome.Assembly getAssembly();
}
