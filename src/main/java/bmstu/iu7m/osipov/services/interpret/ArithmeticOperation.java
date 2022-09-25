package bmstu.iu7m.osipov.services.interpret;

import bmstu.iu7m.osipov.structures.graphs.Elem;

public interface ArithmeticOperation {
    void apply(Elem<Object> item, double value) throws Exception;
}
