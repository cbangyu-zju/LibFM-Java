package kakao.matrix;
import kakao.data.SparseRow;

import sun.misc.Cleaner;
import sun.nio.ch.*;
// import static org.junit.Assert.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


public class LargeSparseMatrix implements Closeable {

	private static final int MAPPING_SIZE = 1 << 30;
	private final RandomAccessFile raf;
	private final int numRows;
	private final int numCols;
	// private int numValues;
	private final List<MappedByteBuffer> mappings = new ArrayList<MappedByteBuffer>();

	public LargeSparseMatrix(String filename, int numRows, int numCols) throws IOException {
		this.raf = new RandomAccessFile(filename, "rw");
		try {
			this.numRows = numRows;
			this.numCols = numCols;
			long size = 8L * numRows * numCols;
			for (long offset = 0; offset < size; offset += MAPPING_SIZE) {
				long size2 = Math.min(size - offset, MAPPING_SIZE);
				mappings.add(raf.getChannel().map(FileChannel.MapMode.READ_WRITE, offset, size2));
			}
		} catch (IOException e) {
			raf.close();
			throw e;
		}
	}
	
	protected long position(int x, int y) {
		return (long) y * numCols + x;
	}
	
	public int numRows() {
		return numRows;
	}
	
	public int numCols() {
		return numCols;
	}
	
	public double get(int x, int y) {
		assert x >= 0 && x < numCols;
		assert y >= 0 && y < numRows;
		long p = position(x,y) * 8;
		int mapN = (int)(p / MAPPING_SIZE);
		int offN = (int)(p % MAPPING_SIZE);
		return mappings.get(mapN).getDouble(offN);
	}
	
	public void set(int x, int y, double d) {
		assert x >= 0 && x < numCols;
		assert y >= 0 && y < numRows;
		long p = position(x,y) * 8;
		int mapN = (int)(p / MAPPING_SIZE);
		int offN = (int)(p % MAPPING_SIZE);
		mappings.get(mapN).putDouble(offN, d);
	}
	
	public void close() throws IOException {
		for (MappedByteBuffer mapping : mappings) {
			clean(mapping);
		}
		raf.close();
	}
	
	private void clean(MappedByteBuffer mapping) {
		if (mapping == null) return;
		Cleaner cleaner = ((DirectBuffer) mapping).cleaner();
		if (cleaner != null) cleaner.clean();
	}
}
