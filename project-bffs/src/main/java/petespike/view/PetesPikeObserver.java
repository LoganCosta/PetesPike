package petespike.view;

import petespike.model.Position;

public interface PetesPikeObserver {
    public void pieceMoved(Position from, Position to);
}
