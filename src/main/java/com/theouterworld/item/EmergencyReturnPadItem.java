package com.theouterworld.item;

import com.theouterworld.registry.ModDimensions;
import com.theouterworld.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class EmergencyReturnPadItem extends Item {
	public EmergencyReturnPadItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		ItemStack stack = user.getStackInHand(hand);
		if (world.isClient()) {
			return ActionResult.PASS;
		}

		ServerPlayerEntity player = (ServerPlayerEntity) user;
		ServerWorld currentWorld = (ServerWorld) world;

		// Only works in the Outer World dimension
		if (!currentWorld.getRegistryKey().equals(ModDimensions.OUTER_WORLD_WORLD_KEY)) {
			return ActionResult.PASS;
		}

		MinecraftServer server = currentWorld.getServer();
		if (server == null) {
			return ActionResult.PASS;
		}

		ServerWorld overworld = server.getWorld(World.OVERWORLD);
		if (overworld == null) {
			return ActionResult.PASS;
		}

		// TODO: Add logic to first try player's spawnpoint (bed/respawn anchor/etc.) before falling back to 0,0
		// This should mirror how Minecraft handles end portal teleportation
		BlockPos origin = new BlockPos(0, 0, 0);
		BlockPos target = findNearestSafeSurface(overworld, origin, 128);
		if (target == null) {
			int y = overworld.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, 0, 0);
			target = new BlockPos(0, Math.max(y, overworld.getBottomY() + 1), 0);
		}

		player.teleport(overworld, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, java.util.Set.of(), player.getYaw(), player.getPitch(), true);

		// Replace with broken emergency return pad instead of consuming
		ItemStack brokenPad = new ItemStack(ModItems.BROKEN_EMERGENCY_RETURN_PAD);
		player.setStackInHand(hand, brokenPad);
		return ActionResult.SUCCESS;
	}

	private static BlockPos findNearestSafeSurface(ServerWorld world, BlockPos origin, int maxRadius) {
		// Check origin first
		BlockPos candidate = surfaceAt(world, origin.getX(), origin.getZ());
		if (isSafe(world, candidate)) {
			return candidate;
		}

		for (int r = 1; r <= maxRadius; r++) {
			// Top and bottom rows of the square ring
			for (int dx = -r; dx <= r; dx++) {
				int x1 = origin.getX() + dx;
				int zTop = origin.getZ() - r;
				int zBottom = origin.getZ() + r;
				if (tryCandidate(world, x1, zTop)) return surfaceAt(world, x1, zTop);
				if (tryCandidate(world, x1, zBottom)) return surfaceAt(world, x1, zBottom);
			}
			// Left and right columns of the square ring (excluding corners already checked)
			for (int dz = -r + 1; dz <= r - 1; dz++) {
				int z1 = origin.getZ() + dz;
				int xLeft = origin.getX() - r;
				int xRight = origin.getX() + r;
				if (tryCandidate(world, xLeft, z1)) return surfaceAt(world, xLeft, z1);
				if (tryCandidate(world, xRight, z1)) return surfaceAt(world, xRight, z1);
			}
		}
		return null;
	}

	private static boolean tryCandidate(ServerWorld world, int x, int z) {
		BlockPos pos = surfaceAt(world, x, z);
		return isSafe(world, pos);
	}

	private static BlockPos surfaceAt(ServerWorld world, int x, int z) {
		int y = world.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
		return new BlockPos(x, y, z);
	}

	private static boolean isSafe(ServerWorld world, BlockPos pos) {
		WorldBorder border = world.getWorldBorder();
		if (!border.contains(pos)) {
			return false;
		}

		BlockState feet = world.getBlockState(pos);
		BlockState head = world.getBlockState(pos.up());
		BlockState ground = world.getBlockState(pos.down());

		if (!feet.isAir() || !head.isAir()) {
			return false;
		}
		if (!ground.isSolidBlock(world, pos.down())) {
			return false;
		}
		if (!world.getFluidState(pos).isEmpty()) {
			return false;
		}
		if (!world.getFluidState(pos.down()).isEmpty()) {
			return false;
		}
		return true;
	}
}


