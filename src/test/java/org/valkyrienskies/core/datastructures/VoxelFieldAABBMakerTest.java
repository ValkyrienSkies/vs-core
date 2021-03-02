package org.valkyrienskies.core.datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.junit.jupiter.api.RepeatedTest;
import org.valkyrienskies.core.VSRandomUtils;

@SuppressWarnings({"UnusedDeclaration", "WeakerAccess"})
public class VoxelFieldAABBMakerTest {

	@RepeatedTest(25)
	public void naiveTest1() {
		NaiveVoxelFieldAABBMaker naive = new NaiveVoxelFieldAABBMaker(0, 0);
		ExtremelyNaiveVoxelFieldAABBMaker extreme = new ExtremelyNaiveVoxelFieldAABBMaker(0, 0);
		Random random = VSRandomUtils.INSTANCE.getDefaultRandomJava();
		for (int i = 0; i < 100; i++) {
			int randomX = random.nextInt(512) - 256;
			int randomY = random.nextInt(256);
			int randomZ = random.nextInt(512) - 256;
			assertEquals(extreme.addVoxel(randomX, randomY, randomZ),
				naive.addVoxel(randomX, randomY, randomZ));
			assertEquals(extreme.makeVoxelFieldAABB(), naive.makeVoxelFieldAABB());
		}

		for (int i = 0; i < 10000; i++) {
			int randomX = random.nextInt(512) - 256;
			int randomY = random.nextInt(256);
			int randomZ = random.nextInt(512) - 256;
			naive.removeVoxel(randomX, randomY, randomZ);
			extreme.removeVoxel(randomX, randomY, randomZ);
			assertEquals(extreme.makeVoxelFieldAABB(), naive.makeVoxelFieldAABB());
		}

		for (int i = 0; i < 100; i++) {
			int randomX = random.nextInt(512) - 256;
			int randomY = random.nextInt(256);
			int randomZ = random.nextInt(512) - 256;
			assertEquals(extreme.addVoxel(randomX, randomY, randomZ),
				naive.addVoxel(randomX, randomY, randomZ));
			assertEquals(extreme.makeVoxelFieldAABB(), naive.makeVoxelFieldAABB());
		}

		for (int i = 0; i < 1000; i++) {
			int randomX = random.nextInt(512) - 256;
			int randomY = random.nextInt(256);
			int randomZ = random.nextInt(512) - 256;
			if (random.nextBoolean()) {
				assertEquals(extreme.addVoxel(randomX, randomY, randomZ),
					naive.addVoxel(randomX, randomY, randomZ));
			} else {
				assertEquals(extreme.removeVoxel(randomX, randomY, randomZ),
					naive.removeVoxel(randomX, randomY, randomZ));
			}
			assertEquals(extreme.makeVoxelFieldAABB(), naive.makeVoxelFieldAABB());
		}
	}

	@RepeatedTest(100)
	public void naiveTest2() {
		Random random = VSRandomUtils.INSTANCE.getDefaultRandomJava();
		Vector3ic centerPos = new Vector3i(random.nextInt() / 100, 0, random.nextInt() / 100);

		NaiveVoxelFieldAABBMaker naive = new NaiveVoxelFieldAABBMaker(centerPos.x(),
			centerPos.z());
		ExtremelyNaiveVoxelFieldAABBMaker extreme = new ExtremelyNaiveVoxelFieldAABBMaker(
			centerPos.x(), centerPos.z());

		List<Vector3ic> blockPosList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			int randomX = random.nextInt(512) - 256 + centerPos.x();
			int randomY = random.nextInt(256) + centerPos.y();
			int randomZ = random.nextInt(512) - 256 + centerPos.z();

			assertEquals(extreme.addVoxel(randomX, randomY, randomZ),
				naive.addVoxel(randomX, randomY, randomZ));
			assertEquals(extreme.makeVoxelFieldAABB(), naive.makeVoxelFieldAABB());
			blockPosList.add(new Vector3i(randomX, randomY, randomZ));
		}

		Collections.shuffle(blockPosList);

		for (Vector3ic pos : blockPosList) {
			int x = pos.x();
			int y = pos.y();
			int z = pos.z();
			assertEquals(extreme.removeVoxel(x, y, z),
				naive.removeVoxel(x, y, z));

			assertEquals(extreme.makeVoxelFieldAABB(), naive.makeVoxelFieldAABB());
		}
	}

}
