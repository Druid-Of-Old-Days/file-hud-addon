package com.example.addon.hud;

import com.example.addon.FileImageHudAddon;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

import static meteordevelopment.meteorclient.utils.Utils.WHITE;
import static meteordevelopment.meteorclient.utils.Utils.set;

public class FileImageHud extends HudElement {
    /**
     * The {@code name} parameter should be in kebab-case.
     */
    SettingGroup sgGeneral = this.settings.getDefaultGroup();
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The size of the marker.")
        .defaultValue(2.0d)
        .range(0.5d, 10.0d)
        .build()
    );

    public static final HudElementInfo<FileImageHud> INFO = new HudElementInfo<>(FileImageHudAddon.HUD_GROUP, "File hud", "here you can load pngs and sometimes jpg's here", FileImageHud::new);

    public FileImageHud() {
        super(INFO);
    }
    private final Setting<String> filePath = sgGeneral.add(new StringSetting.Builder().name("path").defaultValue("").build());
    String currentFile = null;
    NativeImage image = null;
    Identifier ident = null;
    boolean downloaded = false;

    @Override
    public void render(HudRenderer renderer) {
        if(!downloaded){
            if(filePath.get().length()> 3) { // smallest possible filepath length is 4

                image = loadFromFile(filePath.get());
                ident = Identifier.of("123","321");
                if(image==null){
                    downloaded = false;
                    return;
                }
                MinecraftClient.getInstance().getTextureManager().registerTexture(ident,new NativeImageBackedTexture(image));

            }

        }
        if(image != null) {
            if (!Objects.equals(currentFile, filePath.get())) {
                downloaded = false;
            }

            GL.bindTexture(ident);
            Renderer2D.TEXTURE.begin();
            Renderer2D.TEXTURE.texQuad(x, y, image.getWidth() * scale.get(), image.getHeight() * scale.get(), WHITE);
            Renderer2D.TEXTURE.render(null);
            setSize(image.getWidth() * scale.get(),image.getHeight() * scale.get());
        } else {
         setSize(300,300);
        }

    }





    public NativeImage loadFromFile(String path){
        NativeImage returnImage = null;
        try {

            BufferedImage imageToTranslate = ImageIO.read(new File(path));
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(imageToTranslate,"png",outputStream);
            returnImage = NativeImage.read(new ByteArrayInputStream(outputStream.toByteArray()));
            downloaded = true;
            currentFile = path;
        }
        catch (Exception e){
            ChatUtils.error("error has occurred. resetting filepath. Please refresh");
            filePath.reset();
        }
        return  returnImage;
    }






}
