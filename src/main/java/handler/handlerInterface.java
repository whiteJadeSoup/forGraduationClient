package handler;


@FunctionalInterface
interface handlerInterface<R> {
    void dealCallback(R r);
}