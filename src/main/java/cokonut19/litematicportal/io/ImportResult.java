package cokonut19.litematicportal.io;

/**
 * Outcome of an import run.
 *
 * @param moved  number of files that were moved successfully
 * @param failed number of files that could not be moved
 */
public record ImportResult(int moved, int failed) {
}
