package com.deepwelldevelopment.tmos.client;

import com.deepwelldevelopment.tmos.client.gui.GuiWorkbench;
import com.deepwelldevelopment.tmos.common.container.ContainerWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te == null) {
            return new ContainerWorkbench(player.inventory, world, pos);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity te = world.getTileEntity(pos);
        if (te == null) {
            return new GuiWorkbench(player.inventory, world, pos);
        }
        return null;
    }
}
