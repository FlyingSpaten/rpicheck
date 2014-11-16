package de.eidottermihi.rpicheck.beans;

import java.io.Serializable;

/**
 * Wraps the fields of a "df -h" output. Example: <br/>
 * Filesystem Size Used Avail Use% Mounted on <br/>
 * rootfs 3.6G 606M 2.9G 17% / <br/>
 * /dev/root 3.6G 606M 2.9G 17% / <br/>
 * devtmpfs 201M 0 201M 0% /dev <br/>
 * tmpfs 41M 536K 40M 2% /run <br/>
 * tmpfs 5.0M 0 5.0M 0% /run/lock <br/>
 * tmpfs 81M 0 81M 0% /run/shm <br/>
 * /dev/mmcblk0p1 34M 14M 21M 41% /boot
 * 
 * 
 * @author Michael
 * 
 */
public class DiskUsageBean implements Serializable {
	private static final long serialVersionUID = 7826381063640779093L;

	private String fileSystem;
	private String size;
	private String used;
	private String available;
	private String usedPercent;
	private String mountedOn;

	public DiskUsageBean(String fileSystem, String size, String used,
			String available, String usedPercent, String mountedOn) {
		super();
		this.fileSystem = fileSystem;
		this.size = size;
		this.used = used;
		this.available = available;
		this.usedPercent = usedPercent;
		this.mountedOn = mountedOn;
	}

	public String getFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(String fileSystem) {
		this.fileSystem = fileSystem;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getAvailable() {
		return available;
	}

	public void setAvailable(String available) {
		this.available = available;
	}

	public String getUsedPercent() {
		return usedPercent;
	}

	public void setUsedPercent(String usedPercent) {
		this.usedPercent = usedPercent;
	}

	public String getMountedOn() {
		return mountedOn;
	}

	public void setMountedOn(String mountedOn) {
		this.mountedOn = mountedOn;
	}

	@Override
	public String toString() {
		return "DiskUsageBean [fileSystem=" + fileSystem + ", size=" + size
				+ ", used=" + used + ", available=" + available
				+ ", usedPercent=" + usedPercent + ", mountedOn=" + mountedOn
				+ "]";
	}

}
